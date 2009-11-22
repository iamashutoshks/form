/**
 * This file Copyright (c) 2008-2009 Magnolia International
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
import info.magnolia.link.LinkUtil;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templating.RenderingModelImpl;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.processing.FormProcessing;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.form.templates.ParagraphConfig;
import info.magnolia.module.form.util.FormUtil;
import info.magnolia.module.form.validators.Validator;

import org.apache.commons.lang.StringUtils;

import javax.jcr.RepositoryException;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author tmiyar
 *
 */
public class FormModel extends RenderingModelImpl {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FormModel.class);

    private static final String DEFAULT_ERROR_MSG = "generic";
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";
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
                // send mail to admin and confirmation to sender
                FormProcessing processing = FormProcessing.Factory.getDefaultProcessing();
                result = processing.process(((FormParagraph)definition).getFormProcessors(), this);
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
            log.error("Can't process form.",e);
            if(StringUtils.isNotEmpty(result)) {
                errorMessages.put("Error", result);
            } else {
                errorMessages.put("Error", FormUtil.getMessage(DEFAULT_ERROR_MSG));
            }

            return FAILURE;
        }
    }

    private void redirect() throws Exception {

        if(content.hasNodeData("redirect")) {
            String url = content.getNodeData("redirect").getString();
            if(!StringUtils.isEmpty(url)) {

                try {

                    url = LinkUtil.createAbsoluteLink(ContentRepository.WEBSITE, url);
                } catch (RepositoryException e) {
                    log.error("Can't resolve node with uuid " + url);
                    throw new Exception(e);
                }

                ((WebContext)MgnlContext.getInstance()).getResponse().sendRedirect(url);
            }
        }
    }

    private boolean isHoneyPotEmpty() {
        if(StringUtils.isEmpty(MgnlContext.getParameter("field"))) {
            return true;
        }
        return false;
    }

    private void validate() throws Exception {
        Iterator itFieldsets;
        Iterator iterator;
        Content fieldset;
        if (this.getContent().hasContent("fieldsets")) {
            itFieldsets = this.getContent().getContent("fieldsets").getChildren().iterator();

            while (itFieldsets.hasNext()) {
                fieldset = (Content) itFieldsets.next();
                if(fieldset.hasContent("fields")) {
                    iterator = fieldset.getContent("fields").getChildren().iterator();

                    validate(iterator);
                }
            }
        }
    }

    protected void validate(Iterator iterator) throws RepositoryException {
        while (iterator.hasNext()) {
            final Content node = (Content) iterator.next();

            if (node.hasNodeData("controlName")) {

                final String key = node.getNodeData("controlName").getString();
                final String value = MgnlContext.getParameter(key);

                if (StringUtils.isEmpty(value) && isMandatory(node)) {
                    addErrorMessage(key, "mandatory", node);

                } else if (!StringUtils.isEmpty(value) && node.hasNodeData("validation")) {

                    String validation = node.getNodeData("validation").getString();
                    Validator validator = FormModule.getInstance().getValidatorByName(validation);
                    if (validator != null && !validator.validate(value)) {
                        addErrorMessage(key, validator.getName(), node);
                    }
                } else if (node.hasContent(CONTENT_NAME_TEXT_FIELD_GROUP)) {
                    Iterator textFieldGroup = node.getContent(CONTENT_NAME_TEXT_FIELD_GROUP).getChildren().iterator();
                    validate(textFieldGroup);
                }
            }
        } //end while
    }

    protected void addErrorMessage(String field, String message, Content node) {
        String errorMessage = FormUtil.getMessage("form.user.errorMessage." + message, "invalid input");
        errorMessages.put(field, node.getNodeData("title").getString() + ": " + errorMessage);
    }

    protected boolean isMandatory(Content node) {

        return NodeDataUtil.getBoolean(node, "mandatory", false);
    }

    protected boolean hasFormData() {
        final String uuid = MgnlContext.getParameter("paragraphUUID");
        if(uuid != null && uuid.equals(content.getUUID())){
            return true;
        }
        return false;
    }

    public Map getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Map errorMessages) {
        this.errorMessages = errorMessages;
    }

    public String getParagraphsAsStringList() {
        final FormParagraph def = (FormParagraph) this.getDefinition();
        return asStringList(def.getParagraphs());
    }

    public static String asStringList(Collection items){
        StringBuffer list = new StringBuffer();
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            ParagraphConfig def = (ParagraphConfig) iterator.next();
            list.append(def.toString());
            if(iterator.hasNext()){
                list.append(", ");
            }
        }
        return list.toString();
    }

    public String getRequiredSymbol() {
        try {
            return content.getNodeData("requiredSymbol").getString();
        } catch (Exception e) {
            return "";
        }
    }

    public String getRightText() {
        try {
            return content.getNodeData("rightText").getString();
        } catch (Exception e) {
            return "";
        }
    }

}