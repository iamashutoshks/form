/**
 * This file Copyright (c) 2008-2009 Magnolia International
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

import info.magnolia.cms.beans.config.RenderableDefinition;
import info.magnolia.cms.beans.config.RenderingModel;
import info.magnolia.cms.beans.config.RenderingModelImpl;
import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import org.apache.commons.lang.StringUtils;

import javax.jcr.RepositoryException;
import java.util.Map;

/**
 *
 * @author tmiyar
 *
 */
public class FormControlsModel extends RenderingModelImpl {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FormControlsModel.class);

    private String value;
    private String style = "";
    private boolean valid;

    public FormControlsModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
    }

    public String execute() {
        log.debug("Executing {}", this.getClass());
        Validate();
        //set default or user input
        handleValue();
        //set style for error messages
        handleStyle();

        return "";
    }

    private void Validate() {
        valid = true;
        Map errorMessages = this.getFormErrorMessages();
        if (errorMessages != null) {
         // set style
            if (errorMessages.containsKey(content.getNodeData("controlName").getString())) {
                valid = false;
            }
        }

    }

    protected void handleStyle() {
        if (!isValid()) {
            this.style = "class=\"error\"";
        }
        try {
            // TODO: move to specific edit control model class??
            if (StringUtils.isEmpty(this.style)
                    && content.hasNodeData("editLength")) {
                String style2 = content.getNodeData("editLength").getString();
                if (!StringUtils.isEmpty(style2)) {
                    this.style = "class=\"" + style2 + "\"";
                }
            }
        } catch (RepositoryException e) {
            log.debug("can't get style", e);
        }

    }

    public boolean isValid() {
        return valid;
    }

    protected Map getFormErrorMessages() {
        RenderingModelImpl model = this;

        while(model != null) {
            if(model.getParent() instanceof FormModel) {
                return ((FormModel)model.getParent()).getErrorMessages();
            }
            model = (RenderingModelImpl) model.getParent();
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
            val = MgnlContext.getParameterValues(content.getNodeData("controlName").getString());
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