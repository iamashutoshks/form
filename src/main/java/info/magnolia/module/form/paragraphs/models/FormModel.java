/**
 * This file Copyright (c) 2008 Magnolia International
 * Ltd.  (http://www.magnolia.info). All rights reserved.
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
 * is available at http://www.magnolia.info/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.form.paragraphs.models;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.beans.config.RenderableDefinition;
import info.magnolia.cms.beans.config.RenderingModel;
import info.magnolia.cms.beans.config.RenderingModelImpl;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.link.AbsolutePathTransformer;
import info.magnolia.cms.link.UUIDLink;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.processing.FormProcessing;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.form.templates.ParagraphConfig;
import info.magnolia.module.form.validators.Validator;

import org.apache.commons.lang.StringUtils;

import javax.jcr.RepositoryException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author tmiyar
 *
 */
public class FormModel extends RenderingModelImpl {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FormModel.class);

    private static final String MSG_BASENAME = "info.magnolia.module.form.messages";
    private static final String DEFAULT_ERROR_MSG = "generic";
    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";

    private Map errorMessages = new HashMap();

    public FormModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
    }

    public String execute() {
        log.debug("Executing " + this.getClass().getName());
        try {
            if (!hasFormData()) {
                return "";
            }

            validate();

            if (errorMessages.size() == 0 && isHoneyPotEmpty()) {
                // send mail to admin and confirmation to sender
                FormProcessing processing = FormProcessing.Factory.getDefaultProcessing();
                String result = processing.process(((FormParagraph)definition).getFormProcessors(), this);
                if (StringUtils.isEmpty(result)) {
                    redirect();
                    return SUCCESS;
                } else {
                    throw new Exception();
                }
            } else {
                // display validation fields, error message
                return FAILURE;
            }
        } catch (Exception e) {
            log.error("error validating form", e);
            errorMessages.put("Error", getMessage(DEFAULT_ERROR_MSG));
            return FAILURE;
        }
    }

    private void redirect() throws Exception {

        if(content.hasNodeData("redirect")) {
            String url = content.getNodeData("redirect").getString();
            if(!StringUtils.isEmpty(url)) {

                HierarchyManager hm = MgnlContext.getHierarchyManager(ContentRepository.WEBSITE);
                try {
                    Content node = hm.getContentByUUID(url);
                    url = FormModel.createLink(node);
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
                    Validator val = FormModule.getInstance().getValidatorByName(validation);
                    if (val != null && !val.validate(value)) {
                        addErrorMessage(key, val.getName(), node);
                    }
                }
            }
        } //end while
    }

    protected void addErrorMessage(String field, String message, Content node) {
        errorMessages.put(field, node.getNodeData("title").getString() + "  " + getMessage(message));
    }

    protected boolean isMandatory(Content node) {
        boolean mandatory = false;
        try {
            if(node.hasNodeData("mandatory") && node.getNodeData("mandatory").getBoolean()){
                mandatory = true;
            }
        } catch (RepositoryException e) {
            log.debug("Node {} has no 'mandatory' property.", node.getHandle());
        }
        return mandatory;
    }

    protected boolean hasFormData() {
        return (MgnlContext.getPostedForm() != null);
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

    private String getMessage(String key) {
        final Messages messages = MessagesManager.getMessages(MSG_BASENAME);
        return messages.get(key);
    }

    public static String createLink(Content node) {
        if(node == null){
            return null;
        }
        UUIDLink link = new UUIDLink();
        link.setNode(node);
        link.setRepository(node.getHierarchyManager().getName());
        return new AbsolutePathTransformer(true, true, true).transform(link);
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

}