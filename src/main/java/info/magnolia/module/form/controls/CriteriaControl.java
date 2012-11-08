/**
 * This file Copyright (c) 2008-2012 Magnolia International
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
import info.magnolia.freemarker.FreemarkerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * Multi control for specifying feeds and optional titles. Can be used for anything where multi control with two text fields is needed.
 * @deprecated since 1.3.1 use ConditionControl instead.
 */
public class CriteriaControl extends MultiControl {

    private List<OptionConfiguration> conditions;
    /**
     * Called by the template. It renders the dynamic inner row using trim paths templating mechanism.
     */
    public String getInnerHtml() {
        String name = "/" + StringUtils.replace(CriteriaControl.class.getName(), ".", "/") + "Inner.html";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("this", this);
        return FreemarkerUtil.process(name, map);
    }

    /**
     * JS function used to create an object out of the input fields.
     */
    public String getGetObjectFunction() {
        return "function(prefix, index){" +
        " var obj = new Object();" +
        " obj.fieldValue = document.getElementById(prefix + '_fieldValue').value;" +
        " obj.fieldName = document.getElementById(prefix + '_fieldName').value; " +
        " obj.condition = document.getElementById(prefix + '_condition').value; " +
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
        " obj.condition = ''; " +
        " return obj;" +
        "}";
    }

    protected String getEmpty() {
        return "[{fieldValue:'', fieldName:'', condition:''}]";
    }

    public List<OptionConfiguration> getConditions() {
        return conditions;
    }

    public void setConditions(List<OptionConfiguration> conditions) {
        this.conditions = conditions;
    }
    
    public void init(HttpServletRequest request, HttpServletResponse response,
            Content storageNode, Content configNode) throws RepositoryException {
        super.init(request, response, storageNode, configNode);
        // TODO: use Content2Bean
        this.conditions = new ArrayList<OptionConfiguration>();
        for (Iterator<Content> iter = configNode.getContent("options-conditions").getChildren().iterator(); iter.hasNext(); ) {
            Content child = iter.next();
            OptionConfiguration bc = new OptionConfiguration();
            bc.setLabel(child.getNodeData("label").getString());
            bc.setValue(child.getNodeData("value").getString());
            bc.setI18nBasename("info.magnolia.module.form.messages");
            this.conditions.add(bc);
        }
        
    }
}
