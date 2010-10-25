/**
 * This file Copyright (c) 2008-2010 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.form.paragraphs.models;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.link.LinkUtil;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.processing.FormProcessing;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.form.util.FormUtil;
import info.magnolia.module.form.validators.Validator;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templating.RenderingModelImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * RenderingModel for the form. Iterates into sub paragraphs to find and validate form items. If validation succeeds
 * it uses the FormProcessors configured on the paragraph to process the submitted form.
 *
 * @author tmiyar
 */
public class FormModel extends RenderingModelImpl {

    private static final Logger log = LoggerFactory.getLogger(FormModel.class);

    // ActionResult constants passed to form.ftl
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";

    private static final String DEFAULT_ERROR_MSG = "generic";
    private static final String CONTENT_NAME_TEXT_FIELD_GROUP = "edits";

    // Map<String, String>
    private Map errorMessages = new LinkedHashMap();

    public FormModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
    }

    public String execute() {
        log.debug("Executing " + this.getClass().getName());
        String result = "";
        try {
            if (!hasFormData()) {
                return "";
            }

            validate();

            if (errorMessages.size() == 0 && isHoneyPotEmpty()) {

                FormProcessing processing = FormProcessing.Factory.getDefaultProcessing();
                result = processing.process(((FormParagraph) definition).getFormProcessors(), this);
                if (StringUtils.isEmpty(result)) {
                    redirect();
                    return SUCCESS;
                } else {
                    // MGNLFORM-19:
                    throw new Exception();
                }
            } else {
                // display validation fields, error message
                return FAILURE;
            }
        } catch (Exception e) {
            log.error("Can't process form.", e);
            if (StringUtils.isNotEmpty(result)) {
                errorMessages.put("Error", result);
            } else {
                errorMessages.put("Error", FormUtil.getMessage(DEFAULT_ERROR_MSG));
            }

            return FAILURE;
        }
    }

    private void redirect() throws RepositoryException, IOException {

        if (content.hasNodeData("redirect")) {
            String url = content.getNodeData("redirect").getString();
            if (!StringUtils.isEmpty(url)) {

                url = LinkUtil.createAbsoluteLink(ContentRepository.WEBSITE, url);

                ((WebContext) MgnlContext.getInstance()).getResponse().sendRedirect(url);
            }
        }
    }

    protected boolean hasFormData() {
        return StringUtils.equals(MgnlContext.getParameter("paragraphUUID"), content.getUUID());
    }

    private boolean isHoneyPotEmpty() {
        // TODO this cant be needed? hasFormData() performs basically the same thing, namely checking if we're being submitted
        return StringUtils.isEmpty(MgnlContext.getParameter("field"));
    }

    private void validate() throws RepositoryException {
        if (this.getContent().hasContent("fieldsets")) {
            Iterator itFieldsets = this.getContent().getContent("fieldsets").getChildren().iterator();

            while (itFieldsets.hasNext()) {
                Content fieldset = (Content) itFieldsets.next();
                if (fieldset.hasContent("fields")) {
                    Iterator iterator = fieldset.getContent("fields").getChildren().iterator();

                    validate(iterator);
                }
            }
        }
    }

    protected void validate(Iterator iterator) throws RepositoryException {
        while (iterator.hasNext()) {
            final Content node = (Content) iterator.next();

            if (node.hasNodeData("controlName")) {

                final String controlName = node.getNodeData("controlName").getString();
                final String value = MgnlContext.getParameter(controlName);

                if (StringUtils.isEmpty(value) && isMandatory(node)) {
                    addErrorMessage(controlName, "mandatory", node);

                } else if (!StringUtils.isEmpty(value) && node.hasNodeData("validation")) {

                    String validatorName = node.getNodeData("validation").getString();
                    Validator validator = FormModule.getInstance().getValidatorByName(validatorName);
                    if (validator != null && !validator.validate(value)) {
                        addErrorMessage(controlName, validator.getName(), node);
                    }
                } else if (node.hasContent(CONTENT_NAME_TEXT_FIELD_GROUP)) {
                    Iterator textFieldGroup = node.getContent(CONTENT_NAME_TEXT_FIELD_GROUP).getChildren().iterator();
                    validate(textFieldGroup);
                }
            }
        }
    }

    protected void addErrorMessage(String field, String message, Content node) {
        String title = node.getNodeData("title").getString();
        String errorMessage = FormUtil.getMessage("form.user.errorMessage." + message, "invalid input");
        errorMessages.put(field, title + ": " + errorMessage);
    }

    protected boolean isMandatory(Content node) {
        return NodeDataUtil.getBoolean(node, "mandatory", false);
    }

    public Map getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Map errorMessages) {
        this.errorMessages = errorMessages;
    }

    public String getParagraphsAsStringList() {
        FormParagraph paragraph = (FormParagraph) this.getDefinition();
        return StringUtils.join(paragraph.getParagraphs(), ", ");
    }

    public String getRequiredSymbol() {
        return NodeDataUtil.getString(content, "requiredSymbol", "");
    }

    public String getRightText() {
        return NodeDataUtil.getString(content, "rightText", "");
    }
}
