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

import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templating.RenderingModelImpl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Map;

/**
 * RenderingModel for form items. Looks up the parent model to find out if the item passed validation.
 *
 * @author tmiyar
 */
public class FormFieldModel extends RenderingModelImpl {

    private static final Logger log = LoggerFactory.getLogger(FormFieldModel.class);

    private String value;
    private String style = "";
    private boolean valid;

    public FormFieldModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
    }

    public String execute() {
        log.debug("Executing {}", this.getClass());
        validate();
        //set default or user input
        handleValue();
        //set style for error messages
        handleStyle();

        return "";
    }

    // TODO this method doesnt validate, it checks the parent for error messages for this field

    private void validate() {
        valid = true;
        Map errorMessages = getFormErrorMessages();
        if (errorMessages != null) {
            if (errorMessages.containsKey(getControlName())) {
                valid = false;
            }
        }
    }

    protected void handleStyle() {
        String cssClass = "";
        if (!isValid()) {
            cssClass = "error";
        }
        try {
            // TODO: move to specific edit control model class??
            if (content.hasNodeData("editLength")) {
                String style2 = content.getNodeData("editLength").getString();
                if (!StringUtils.isEmpty(style2)) {
                    if (!StringUtils.isEmpty(cssClass)) {
                        cssClass = cssClass + " " + style2;
                    } else {
                        cssClass = style2;
                    }

                }
            }
        } catch (RepositoryException e) {
            log.debug("failed to get edit control style", e);
        }

        if (StringUtils.isNotBlank(cssClass)) {
            style = "class=\"" + cssClass + "\"";
        }
    }

    public boolean isValid() {
        return valid;
    }

    // Map<String, String>

    public String getValue() {
        return value;
    }

    protected void handleValue() {
        String[] val = null;
        try {
            val = MgnlContext.getParameterValues(getControlName());
            if (val == null) {
                //has default value?
                if (content.hasNodeData("default")) {
                    val = new String[]{content.getNodeData("default").getString()};
                }
            }
        } catch (RepositoryException e) {
        }

        this.value = (val != null) ? StringUtils.join(val, "*") : "";
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

    public String getRightText() {
        return getFormModel().getRightText();
    }

    public String getRequiredSymbol() {
        return getFormModel().getRequiredSymbol();
    }

    protected Map getFormErrorMessages() {
        return getFormModel().getErrorMessages();
    }

    private String getControlName() {
        return content.getNodeData("controlName").getString();
    }

    private FormModel getFormModel() {
        RenderingModel model = this.parentModel;
        while (model != null) {
            if (model instanceof FormModel)
                return (FormModel) model;
            model = model.getParent();
        }
        return null;
    }
}
