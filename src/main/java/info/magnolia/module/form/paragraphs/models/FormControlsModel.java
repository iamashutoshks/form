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

        //set style for error messages
        handleStyle();

        return "";
    }

    private void handleStyle() {
        if (this.parentModel instanceof FormModel) {
            FormModel formModel = (FormModel) this.parentModel;

            // set style
            if (formModel.getErrorMessages().containsKey(content.getNodeData("controlName").getString())) {
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

    public String getValue() {
        return value;
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