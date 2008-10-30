/**
 * This file Copyright (c) 2007-2008 Magnolia International
 * Ltd.  (http://www.magnolia.info). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia.info/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.form.paragraphs.models;

import info.magnolia.cms.beans.config.Renderable;
import info.magnolia.cms.beans.config.RenderingModel;
import info.magnolia.cms.beans.config.RenderingModelImpl;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.RequestFormUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.RequestProcessor;
import info.magnolia.module.form.validations.Validation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author tmiyar
 *
 */
public class FormModel extends RenderingModelImpl{

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FormModel.class);

    private Map errorMessages = new HashMap();

    public FormModel(Content content, Renderable renderable, RenderingModel parent) {
        super(content, renderable, parent);
    }

    public String execute() {
        log.debug("Executing " + this.getClass().getName());
        try {
            if (!hasFormData()) {
                return "";
            }

            validate();

            if (errorMessages.size() == 0) {
                // send mail to admin and confirmation to sender
                RequestProcessor.sendMail(MgnlContext.getParameters(), content);
                return "success";
            } else {
                // display validation fields, error message

                return "failed";
            }
        } catch (Exception e) {
            log.error("error validating form", e);
            return "failed";
        }
    }

    private void validate() throws Exception {

        if (this.getContent().hasContent("controls")) {
            Iterator iterator = this.getContent().getContent("controls").getChildren().iterator();

            validate(iterator);
        }
    }

    protected void validate(Iterator iterator) throws RepositoryException {
        while (iterator.hasNext()) {
            final Content node = (Content) iterator.next();

            if (node.hasNodeData("controlName")) {

                final String key = node.getNodeData("controlName").getString();
                final String value = MgnlContext.getParameter(key);

                if (StringUtils.isEmpty(value) && isMandatory(node)) {
                    addErrorMessage(key, content.getNodeData("mandatoryErrorMessage").getString(), node);

                } else if (!StringUtils.isEmpty(value) && node.hasNodeData("validation")) {

                    String validation = node.getNodeData("validation").getString();
                    Validation val = FormModule.getInstance().getValidatorByName(validation);
                    if (val != null && !val.validate(value)) {
                        addErrorMessage(key, val.getMessage(), node);
                    }
                }
            }
        } //end while
    }

    protected void addErrorMessage(String field, String message, Content node) {
            errorMessages.put(field, node.getNodeData("title").getString() + "  " + message);

    }

    protected boolean isMandatory(Content node) {
        boolean mandatory = false;
        try {
            if(node.hasNodeData("mandatory") && node.getNodeData("mandatory").getBoolean()){
                mandatory = true;
            }
        } catch (RepositoryException e) {
            log.debug("node has no mandatory property" + node.getHandle());
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

}