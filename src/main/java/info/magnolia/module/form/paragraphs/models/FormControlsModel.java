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

import java.util.Map;

import info.magnolia.cms.beans.config.Renderable;
import info.magnolia.cms.beans.config.RenderingModel;
import info.magnolia.cms.beans.config.RenderingModelImpl;
import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 *
 * @author tmiyar
 *
 */
public class FormControlsModel extends RenderingModelImpl {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory
            .getLogger(FormControlsModel.class);

    private String value;
    private String style = "";

    public FormControlsModel(Content content, Renderable renderable, RenderingModel parent) {
        super(content, renderable, parent);
    }

    public String execute() {
        log.debug("Executing " + this.getClass().getName());
        //set default or user input
        handleValue();
        //set style for error messages
        handleStyle();

        return "";
    }

    protected void handleStyle() {
        Map errorMessages = this.getFormErrorMessages();
        if (errorMessages != null) {

            // set style
            if (errorMessages.containsKey(content.getNodeData("controlName").getString())) {
                this.style = "class=\"error\"";
            }
            try {
                //TODO: move to specific edit control model class??
                if (StringUtils.isEmpty(this.style) && content.hasNodeData("editLength")) {
                    String style2 = content.getNodeData("editLength").getString();
                    if (!StringUtils.isEmpty(style2)) {
                        this.style = "class=\"" + style2 + "\"";
                    }
                }
            } catch (RepositoryException e) {
                log.debug("can't get style", e);
            }
        }
    }

    protected Map getFormErrorMessages() {
        RenderingModelImpl model = this;

        while(model != null) {
            if(model.getParentModel() instanceof FormModel) {
                return ((FormModel)model.getParentModel()).getErrorMessages();
            }
            model = (RenderingModelImpl) model.getParentModel();
        }
        return null;
    }



    public String getValue() {

        return value;
    }

    protected void handleValue() {
        //has default value?
        String[] val = null;
        try {
            val = MgnlContext.getParameterValues(this.content.getNodeData("controlName").getString());
            if(val == null) {
                if(content.hasNodeData("default")) {
                    val = new String[]{content.getNodeData("default").getString()};
                }
            }
        } catch (RepositoryException e) {

        }
        if(val == null) {
            val = new String[]{""};
        }
        this.value = StringUtils.join(val, "*");
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

}