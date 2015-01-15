/**
 * This file Copyright (c) 2013-2015 Magnolia International
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
package info.magnolia.module.form.setup.migration;

import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.ui.dialog.setup.migration.ControlMigrator;
import info.magnolia.ui.form.field.definition.CompositeFieldDefinition;
import info.magnolia.ui.form.field.definition.MultiValueFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.TextFieldDefinition;
import info.magnolia.ui.form.field.transformer.composite.NoOpCompositeTransformer;
import info.magnolia.ui.form.field.transformer.multi.MultiValueSubChildrenNodePropertiesTransformer;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Migrate Conditional control to a Multi Filed defining CompositeField as field.<br>
 * <code>
 * MultiField field Defintion <br>
 * - Field <br>
 * --   CompositeField Definition <br>
 * ---      Fields <br>
 * ----        OptionField definition (Condition)<br>
 * ----        TextField Definition (Field Name)<br>
 * ----        TextField Definition (Field Value)<br>
 * </code>
 */
public class ConditionalControlMigrator implements ControlMigrator {

    @Override
    public void migrate(Node controleNode, InstallContext installContext) throws RepositoryException {
        // Set MultiField Configuration
        initMultiFieldConfiguration(controleNode);

        // Set Composite Field Configuration
        initFieldForMultiFieldConfiguration(controleNode);

        // Set Option as an element of the Composite Field
        initSelectField(controleNode);

        // Set Text Field Name as an element of the Composite Field
        initTextField(controleNode, "fieldName");
        // Set Text Field Value as an element of the Composite Field
        initTextField(controleNode, "fieldValue");
    }

    /**
     * Create the appropriate class and transformerClass property configuration for a MultiField Definition.
     */
    private void initMultiFieldConfiguration(Node controleNode) throws RepositoryException {
        // Set appropriate Field Definition
        controleNode.setProperty("class", MultiValueFieldDefinition.class.getName());
        // Add transformerClass
        controleNode.setProperty("transformerClass", MultiValueSubChildrenNodePropertiesTransformer.class.getName());

        // Add a Field Node (used to configure the Multi field)
        controleNode.addNode("field", NodeTypes.ContentNode.NAME);

        // Remove no more used property
        if(controleNode.hasProperty("boxType")) {
            controleNode.getProperty("boxType").remove();
        }
        if(controleNode.hasProperty("controlType")) {
            controleNode.getProperty("controlType").remove();
        }
    }

    /**
     * Create the appropriate class and transformerClass property configuration for a CompositeField Definition.
     */
    private void initFieldForMultiFieldConfiguration(Node controleNode) throws RepositoryException {
        Node fieldNode = controleNode.getNode("field");
        // Set appropriate Field Definition
        fieldNode.setProperty("class", CompositeFieldDefinition.class.getName());
        // Add transformerClass
        fieldNode.setProperty("transformerClass", NoOpCompositeTransformer.class.getName());

        // Add a Field Node (used to configure the Multi field)
        fieldNode.addNode("fields", NodeTypes.ContentNode.NAME);
    }

    private void initSelectField(Node controleNode) throws RepositoryException {
        Node condition = controleNode.getNode("field/fields").addNode("condition", NodeTypes.ContentNode.NAME);
        condition.setProperty("class", SelectFieldDefinition.class.getName());
        condition.setProperty("label", "dialog.form.condition.tabMain.condition.option.label");
        // Add an Option node
        Node optionsList = condition.addNode("options", NodeTypes.ContentNode.NAME);

        Node optionsConditionsNode = controleNode.getNode("options-conditions");
        Iterator<Node> nodes = NodeUtil.getNodes(optionsConditionsNode).iterator();
        while (nodes.hasNext()) {
            // Move
            NodeUtil.moveNode(nodes.next(), optionsList);
        }
    }

    private void initTextField(Node controleNode, String nodeName) throws RepositoryException {
        Node text = controleNode.getNode("field/fields").addNode(nodeName, NodeTypes.ContentNode.NAME);
        text.setProperty("class", TextFieldDefinition.class.getName());
        if (nodeName.equals("fieldName")) {
            text.setProperty("label", "dialog.form.condition.tabMain.condition.name.label");
        } else {
            text.setProperty("label", "dialog.form.condition.tabMain.condition.value.label");
        }
    }

}
