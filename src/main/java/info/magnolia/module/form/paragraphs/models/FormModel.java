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

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.beans.config.Renderable;
import info.magnolia.cms.beans.config.RenderingModel;
import info.magnolia.cms.beans.config.RenderingModelImpl;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.module.baukasten.templates.MainTemplateModel;
import info.magnolia.module.baukasten.util.BaukastenUtil;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.RequestProcessor;
import info.magnolia.module.form.validations.Validation;

import java.io.IOException;
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
public class FormModel extends MainTemplateModel{

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

            if (errorMessages.size() == 0 && this.getContent().hasNodeData("requestProcessor") && isHoneyPotEmpty()) {
                // send mail to admin and confirmation to sender
                RequestProcessor processor = FormModule.getInstance().getRequestProcessor("default");
                processor.process(content);
                redirect();
                return "success";
            } else {
                // display validation fields, error message
                return "failed";
            }
        } catch (Exception e) {
            log.error("error validating form", e);
            errorMessages.put("Error",MgnlContext.getMessages("info.magnolia.module.form.messages").get("generic") );
            return "failed";
        }
    }

    private void redirect() throws Exception {

        if(content.hasNodeData("redirect")) {
            String url = content.getNodeData("redirect").getString();
            if(!StringUtils.isEmpty(url)) {
                boolean external = content.getNodeData("external").getBoolean();
                if(external) {
                    if(!url.startsWith("http://")){
                        url = "http://"+url;
                    }
                } else {
                    HierarchyManager hm = MgnlContext.getHierarchyManager(ContentRepository.WEBSITE);
                    try {
                        url = BaukastenUtil.createLink(hm.getContent(url));
                    } catch (RepositoryException e) {
                        log.error("Can't resolve node with uuid " + url);
                        throw new Exception(e);
                    }
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
                    addErrorMessage(key, "mandatory", node);

                } else if (!StringUtils.isEmpty(value) && node.hasNodeData("validation")) {

                    String validation = node.getNodeData("validation").getString();
                    Validation val = FormModule.getInstance().getValidatorByName(validation);
                    if (val != null && !val.validate(value)) {
                        addErrorMessage(key, val.getName(), node);
                    }
                }
            }
        } //end while
    }

    protected void addErrorMessage(String field, String message, Content node) {

        errorMessages.put(field, node.getNodeData("title").getString() + "  "
                + MgnlContext.getMessages("info.magnolia.module.form.messages").get(message));

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