/**
 * This file Copyright (c) 2008-2011 Magnolia International
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
package info.magnolia.module.form.controls;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.NodeData;
import info.magnolia.cms.gui.control.Button;
import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.gui.dialog.DialogBox;
import info.magnolia.freemarker.FreemarkerUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common class for creating combined multi controls.
 * @author had
 *
 */
public abstract class MultiControl extends DialogBox implements MultiValueControl {
    private static final Logger log = LoggerFactory.getLogger(MultiControl.class);

    /**
     * Create the object to initialize the table.
     */
    public String getJSON() {
        final List<String> values = this.getValues();

        if (values.size() == 0) {
            return getEmpty();
        }

        // enclose each value with {}
        final List<String> objects = new ArrayList<String>();
        for (Iterator<String> iter = values.iterator(); iter.hasNext();) {
            final String value = iter.next();
            log.debug("value:" + value);
            objects.add("{" + value + "}");
        }
        return "[" + StringUtils.join(objects.iterator(), ",") + "]";
    }

    protected abstract String getEmpty();

    protected List<String> readValues() {
        List<String> values = new ArrayList<String>();
        if (this.getStorageNode() != null) {
            try {
                Content data = this.getStorageNode().getContent(getName());
                Iterator<Content> iter = data.getChildren().iterator();
                while (iter.hasNext()) {
                    Content value = iter.next();
                    Iterator<NodeData> dataVals = value.getNodeDataCollection().iterator();
                    StringBuffer buf = new StringBuffer();
                    while (dataVals.hasNext()) {
                        NodeData partial = dataVals.next();
                        if (buf.length() > 0) {
                            buf.append(",");
                        }
                            buf.append(partial.getName()).append(":'").append(partial.getString()).append("'");
                    }
                    values.add(buf.toString());
                }
            } catch (PathNotFoundException e) {
                // not yet existing: OK
            }
            catch (RepositoryException re) {
                log.error("can't set values", re);
            }
        }
        return values;
    }

    public void drawHtml(Writer w) throws IOException {
        PrintWriter out = (PrintWriter) w;
        this.drawHtmlPre(out);
        out.write(FreemarkerUtil.process(MultiControl.class, this));
        this.drawHtmlPost(out);

    }

    public String getSaveInfo() {
        Boolean renderSaveInfo = BooleanUtils.toBooleanObject(this.getConfigValue("saveInfo"));
        if (BooleanUtils.toBooleanDefaultIfNull(renderSaveInfo, true)) {
            ControlImpl dummy = new ControlImpl(this.getName(), (String) null);
            //dummy.setValueType(ControlImpl.VALUETYPE_MULTIPLE);
            return dummy.getHtmlSaveInfo();
        }
        // don' create the save info
        return "";
    }

    /**
     * The button to add a new row.
     */
    public String getAddButton() {
        Button add = new Button();
        add.setLabel(getMessage("buttons.add"));
        add.setOnclick(this.getName() + "DynamicTable.addNew();");
        add.setSmall(true);
        return add.getHtml();
    }

    /**
     * Button for deleting a row.
     */
    public String getDeleteButton() {
        Button delete = new Button();
        delete.setLabel(this.getMessage("buttons.delete"));
        delete.setOnclick(this.getName() + "DynamicTable.del('${index}');" + this.getName() + "DynamicTable.persist();");
        delete.setSmall(true);
        return delete.getHtml();
    }

    /**
     * If the values are saved using the valueType multiple, we can not use the same name for the hidden field we use
     * for persisting the data.
     * @return the name of the hidden field
     */
    public String getHiddenFieldName() {
        return this.getName();
    }

    /**
     * JS function used to create an object out of the input fields.
     */
    public String getGetObjectFunction() {
        return "function(prefix, index){" +
        " var obj = new Object();" +
        " obj.link = document.getElementById(prefix + '_fieldValue').value;" +
        " obj.title = document.getElementById(prefix + '_fieldName').value; " +
        " return obj;" +
        "}";
    }

    /**
     * JS function used to create a new empty object.
     */
    public String getNewObjectFunction() {
        return "function(){" +
        " var obj = new Object();" +
        " obj.fieldValue = ''; " +
        " obj.fieldName = ''; " +
        " return obj;" +
        "}";
    }
}
