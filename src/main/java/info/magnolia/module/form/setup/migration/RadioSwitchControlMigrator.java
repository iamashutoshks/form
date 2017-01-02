/**
 * This file Copyright (c) 2013-2017 Magnolia International
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
import info.magnolia.objectfactory.Components;
import info.magnolia.ui.dialog.setup.migration.ControlMigrator;
import info.magnolia.ui.dialog.setup.migration.ControlMigratorsRegistry;
import info.magnolia.ui.form.field.definition.StaticFieldDefinition;
import info.magnolia.ui.form.field.definition.SwitchableFieldDefinition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Migrate a ConditionalControl to a Switchable Field.
 */
public class RadioSwitchControlMigrator implements ControlMigrator {

    private static final Logger log = LoggerFactory.getLogger(RadioSwitchControlMigrator.class);
    private Map<String, ControlMigrator> controlMigrationMap;

    public RadioSwitchControlMigrator() {
        this.controlMigrationMap = Components.getComponent(ControlMigratorsRegistry.class).getAllMigrators();
    }

    /**
     * Steps <br>
     * - Migrate Options <br>
     * -- Build and fill the Fields set<br>
     * - Handle Fields set. <br>
     */
    @Override
    public void migrate(Node controlNode, InstallContext installContext) throws RepositoryException {
        HashMap<String, Node> optionFieldsMap = new HashMap<String, Node>();

        controlNode.getProperty("controlType").remove();
        controlNode.setProperty("class", SwitchableFieldDefinition.class.getName());

        if (controlNode.hasNode("options")) {
            Node options = controlNode.getNode("options");
            // Handle Options
            handleOptions(options, optionFieldsMap);
            // Handle Fields
            handleFields(controlNode, optionFieldsMap, installContext);
            // Remove the option controls nodes.
            removeOptionControls(optionFieldsMap);
        } else {
            log.error("This control has no Options.... Nothing will be migrated.");
        }

    }

    private void handleFields(Node controlNode, HashMap<String, Node> optionFieldsMap, InstallContext installContext) throws RepositoryException {
        // Init
        Workspace workspace = controlNode.getSession().getWorkspace();
        // create the root Fields Node
        Node fields = controlNode.addNode("fields", NodeTypes.ContentNode.NAME);
        fields.getSession().save();

        // Duplicate option/controls definition to fields
        Collection<Node> controls = optionFieldsMap.values();
        for (Node control : controls) {
            workspace.copy(control.getPath(), fields.getPath() + "/" + control.getName());
        }
        fields.getSession().save();

        // Iterate the newly created controles and migrate them
        Iterator<Node> controlsIterator = NodeUtil.getNodes(fields, NodeTypes.ContentNode.NAME).iterator();
        while (controlsIterator.hasNext()) {
            handleField(controlsIterator.next(), installContext);
        }

    }

    private void handleField(Node fieldNode, InstallContext installContext) throws RepositoryException {
        if (fieldNode.hasProperty("controlType")) {
            String controlTypeName = fieldNode.getProperty("controlType").getString();

            if (controlMigrationMap.containsKey(controlTypeName)) {
                ControlMigrator controlMigration = controlMigrationMap.get(controlTypeName);
                controlMigration.migrate(fieldNode, installContext);
            } else {
                fieldNode.setProperty("class", StaticFieldDefinition.class.getName());
                if (!fieldNode.hasProperty("value")) {
                    fieldNode.setProperty("value", "Field not yet supported");
                }
                log.warn("No field defined for control '{}' for node '{}'", controlTypeName, fieldNode.getPath());
            }
        } else {
            fieldNode.setProperty("class", StaticFieldDefinition.class.getName());
            if (!fieldNode.hasProperty("value")) {
                fieldNode.setProperty("value", "Field not yet supported");
            }
            log.warn("No 'controlType' defined for control {}. This control will not be migrated", fieldNode.getPath());
        }
    }

    /**
     * Remove teh controls node from the option node.
     */
    private void removeOptionControls(HashMap<String, Node> optionFieldsMap) throws RepositoryException {
        Collection<Node> controls = optionFieldsMap.values();
        for (Node control : controls) {
            log.info("Removed controls definition from the following option {} ", control.getParent().getPath());
            Node parent = control.getParent();
            parent.remove();
            parent.getSession().save();
        }
    }

    /**
     * Iterate the Options <br>
     * For every option: <br>
     * - From the controls node (contain the Field definition linked to the current option) initialize <br>
     * optionFieldsMap with a field Definition.<br>
     * - Remove this controls node <br>
     * - Create a standard Option definition.
     */
    private void handleOptions(Node options, HashMap<String, Node> optionFieldsMap) throws RepositoryException {
        List<Node> optionsList = NodeUtil.asList(NodeUtil.getNodes(options, NodeTypes.ContentNode.NAME));
        if (optionsList.size() < 1) {
            log.warn("{} has no option defined. Field will not be migrated", options.getPath());
            return;
        }

        for (Node option : optionsList) {
            String relatredControlname = handleOptionControls(option, optionFieldsMap);
            // Create a Value property pointing to the Field Node name definition.
            if (StringUtils.isNotBlank(relatredControlname)) {
                option.setProperty("value", relatredControlname);
            }
        }
    }

    /**
     * Put the related control definition in the optionFieldsMap. <br>
     * Call specificControlHandling in case of custom controls operation are needed. <br>
     *
     * @return the related control name.
     */
    private String handleOptionControls(Node option, HashMap<String, Node> optionFieldsMap) throws RepositoryException {
        String relatredControlname = null;
        if (option.hasNode("controls")) {
            List<Node> controls = NodeUtil.asList(NodeUtil.getNodes(option.getNode("controls"), NodeTypes.ContentNode.NAME));

            if (controls.size() > 1) {
                log.warn("{} do not support multiple field per option. the following control will not be migrated {} ", SwitchableFieldDefinition.class.getName(), option.getPath());
                return null;
            }
            Node control = controls.get(0);
            // Call specific handling.
            specificControlHandling(control);
            relatredControlname = control.getName();
            optionFieldsMap.put(relatredControlname, control);

        } else {
            log.warn("{} options has no controls defined. No related field will be created", option.getPath());
            return null;
        }
        return relatredControlname;
    }

    /**
     * For Form. <br>
     * - in case of fckEditor <br>
     * Change control to Code Edit <br>
     * Add a language property equivalent to the control name (html, text,...)
     */
    protected void specificControlHandling(Node control) throws RepositoryException {
        if (control.hasProperty("controlType")) {
            String controlType = control.getProperty("controlType").getString();
            if (controlType.equals("fckEdit")) {
                control.getProperty("controlType").setValue("editCode");
                control.setProperty("language", control.getName());
            }
        }
    }

}
