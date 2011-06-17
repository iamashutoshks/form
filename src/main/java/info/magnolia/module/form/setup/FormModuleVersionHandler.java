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
package info.magnolia.module.form.setup;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.ItemType;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.delta.ArrayDelegateTask;
import info.magnolia.module.delta.BootstrapSingleModuleResource;
import info.magnolia.module.delta.CheckAndModifyPropertyValueTask;
import info.magnolia.module.delta.CreateNodeTask;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.NewPropertyTask;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.PropertyExistsDelegateTask;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.WarnTask;
import info.magnolia.module.form.setup.for1_2.UpdateDialogDefinitionFor1_2;
import info.magnolia.module.form.setup.for1_2_1.UpdateDialogDefinitionsFor1_2_1;
import info.magnolia.nodebuilder.task.ErrorHandling;
import info.magnolia.nodebuilder.task.ModuleNodeBuilderTask;
import info.magnolia.nodebuilder.task.NodeBuilderTask;
import static info.magnolia.nodebuilder.Ops.*;

/**
 * VersionHandler for the form module.
 *
 * @author tmiyar
 */
public class FormModuleVersionHandler extends DefaultModuleVersionHandler {

    public FormModuleVersionHandler() {
        register(DeltaBuilder.update("1.1", "Adds support for hidden and password fields")
                .addTask(new ArrayDelegateTask("Update grouped edit fields styles.", "",
                        new CheckAndModifyPropertyValueTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/editLength/options/s", "value", "s", "form-item-s"),
                        new CheckAndModifyPropertyValueTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/editLength/options/m", "value", "m", "form-item-m"),
                        new CheckAndModifyPropertyValueTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/editLength/options/l", "value", "l", "form-item-l")))
                .addTask(new BootstrapSingleModuleResource("", "", "config.modules.form.dialogs.formHidden.xml"))
                .addTask(new BootstrapSingleModuleResource("", "", "config.modules.form.paragraphs.formPassword.xml"))
                .addTask(new BootstrapSingleModuleResource("", "", "config.modules.form.paragraphs.formHidden.xml"))
                .addTask(new ModuleNodeBuilderTask("", "", ErrorHandling.logging,
                getNode("paragraphs/form").then(
                        getNode("paragraphs").then(
                                addNode("formPassword", ItemType.CONTENTNODE).then(
                                        addProperty("name", "formPassword")
                                ),
                                addNode("formHidden", ItemType.CONTENTNODE).then(
                                        addProperty("name", "formHidden")
                                )
                        ),
                        addNode("parameters", ItemType.CONTENTNODE).then(
                                addProperty("formEnctype", "multipart/form-data")
                        )
                ),
                getNode("config/validators").then(
                        addNode("password", ItemType.CONTENTNODE).then(
                                addProperty("class", "info.magnolia.module.form.validators.PasswordValidator")
                        )
                )
        )));

        register(DeltaBuilder.update("1.1.1", "")
                .addTask(new BootstrapSingleModuleResource("Add noHTML validator", "", "config.modules.form.config.validators.noHTML.xml"))
                .addTask(new ArrayDelegateTask("Add maxLength for formEdit", "",
                        new CreateNodeTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formEdit/tabMain", "maxLength", ItemType.CONTENTNODE.getSystemName()),
                        new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formEdit/tabMain/maxLength", "controlType", "edit"),
                        new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formEdit/tabMain/maxLength", "description", "dialog.form.edit.tabMain.maxLength.description"),
                        new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formEdit/tabMain/maxLength", "label", "dialog.form.edit.tabMain.maxLength.label"),
                        new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formEdit/tabMain/maxLength", "rows", "1"),
                        new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formEdit/tabMain/maxLength", "type", "String")))
                .addTask(new ArrayDelegateTask("Add maxLength for formGroupEditItem", "",
                new CreateNodeTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain", "maxLength", ItemType.CONTENTNODE.getSystemName()),
                new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/maxLength", "controlType", "edit"),
                new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/maxLength", "description", "dialog.form.edit.tabMain.maxLength.description"),
                new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/maxLength", "label", "dialog.form.edit.tabMain.maxLength.label"),
                new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/maxLength", "rows", "1"),
                new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/maxLength", "type", "String")))
        );

        register(DeltaBuilder.update("1.1.4", "")
                .addTask(new ArrayDelegateTask("Add mandatory field to formSelection", "",
                        new CreateNodeTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formSelection/tabMain", "mandatory", ItemType.CONTENTNODE.getSystemName()),
                        new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formSelection/tabMain/mandatory", "buttonLabel", "dialog.form.edit.tabMain.mandatory.buttonLabel"),
                        new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formSelection/tabMain/mandatory", "controlType", "checkboxSwitch"),
                        new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formSelection/tabMain/mandatory", "label", "dialog.form.edit.tabMain.mandatory.label"),
                        new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formSelection/tabMain/mandatory", "selected", "false"),
                        new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formSelection/tabMain/mandatory", "type", "Boolean")))
                .addTask(new ArrayDelegateTask("Add paragraphs and dialogs for multi step forms", "",
                new BootstrapSingleModuleResource("", "", "config.modules.form.paragraphs.formStep.xml"),
                new BootstrapSingleModuleResource("", "", "config.modules.form.dialogs.formStep.xml")))
        );

        register(DeltaBuilder.update("1.2", "")
                .addTask(new NodeExistsDelegateTask("Description", "Checks if description node is present in formFile dialog config otherwise creates one with properties.", ContentRepository.CONFIG, "/modules/form/dialogs/formFile/tabMain/description", new WarnTask("Description", "Node description already present in formFile dialog config."), (new ArrayDelegateTask("Description", "Adds description field to formFile.",
                    new CreateNodeTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formFile/tabMain", "description", ItemType.CONTENTNODE.getSystemName()),
                    new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formFile/tabMain/description", "controlType", "edit"),
                    new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formFile/tabMain/description", "description", "dialog.form.file.tabMain.description.description"),
                    new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formFile/tabMain/description", "label", "dialog.form.file.tabMain.description.label"),
                    new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formFile/tabMain/description", "rows", "1"),
                    new NewPropertyTask("", "", ContentRepository.CONFIG, "/modules/form/dialogs/formFile/tabMain/description", "type", "String")))))
                .addTask(new UpdateDialogDefinitionFor1_2())
                .addTask(new PropertyExistsDelegateTask("Required", "Checks if required property is present in controlName config of form selection dialog otherwise creates one with true value.", ContentRepository.CONFIG, "/modules/form/dialogs/formSelection/tabMain/controlName", "required", new WarnTask("Required", "Required property already present in controlName config of form selection dialog."), new NewPropertyTask("Required", "Adds required property to controlName config of form selection dialog.", ContentRepository.CONFIG, "/modules/form/dialogs/formSelection/tabMain/controlName", "required", "true")))
        );

        register(DeltaBuilder.update("1.2.1", "")
                .addTask(new UpdateDialogDefinitionsFor1_2_1())
                .addTask(new RemoveNodeTask("Remove node", "Removes unused 'Display Required Symbol' option", ContentRepository.CONFIG, "/modules/form/dialogs/formGroupFields/tabMain/displayRequiredSymbol"))
                .addTask(new CheckAndModifyPropertyValueTask("Fix validator regex", "Fixes number validator regex", ContentRepository.CONFIG, "/modules/form/config/validators/number", "expression", "[0-9]*", "^[0-9]*$"))
        );
        
        register(DeltaBuilder.update("1.3", "")
                .addTask(new ArrayDelegateTask("Add new formCriteria Paragraph", "Paragraph used to select next step for multistep forms.",
                        new BootstrapSingleModuleResource("", "", "config.modules.form.dialogs.formCriteria.xml"),
                        new BootstrapSingleModuleResource("", "", "config.modules.form.paragraphs.formCriteria.xml"),
                        new NodeBuilderTask("", "", ErrorHandling.strict, "config", 
                                getNode("modules/form/paragraphs/form/paragraphs").then(
                                        addNode("formCriteria", ItemType.CONTENTNODE).then(
                                                addProperty("name", "formCriteria")
                                                )))))
                        
                .addTask(new NodeExistsDelegateTask("Add back button", "Paragraph form submit has the option to display a back button.",
                        "config", "/modules/form/dialogs/formSubmit/tabMain/backButtonText", null,
                        new NodeBuilderTask("", "", ErrorHandling.strict, "config", 
                                getNode("modules/form/dialogs/formSubmit/tabMain").then(
                                        addNode("backButtonText", ItemType.CONTENTNODE).then(
                                                addProperty("controlType", "edit"),
                                                addProperty("description", "dialog.form.submit.tabMain.backButtonText.description"),
                                                addProperty("i18n", "true"),
                                                addProperty("label", "dialog.form.submit.tabMain.backButtonText.label"),
                                                addProperty("rows", "1"),
                                                addProperty("type", "String")
                                                )))))
                .addTask(new ArrayDelegateTask("Add summary paragraph", "", new BootstrapSingleModuleResource("", "", "config.modules.form.paragraphs.formSummary.xml"),
                        new BootstrapSingleModuleResource("", "", "config.modules.form.dialogs.formSummary.xml"),
                        new NodeBuilderTask("", "", ErrorHandling.strict, "config", 
                                getNode("modules/form/paragraphs/form/paragraphs").then(
                                        addNode("formSummary", ItemType.CONTENTNODE).then(
                                                addProperty("name", "formSummary")
                                                )))))
                .addTask(new NodeBuilderTask("Control to display form parameters", "New control that will display the parameters to use in freemarker syntax.", ErrorHandling.strict, "config", 
                        getNode("modules/form/dialogs/form").then(
                                getNode("tabContactEmail").then(
                                    addNode("freemarkerParams", ItemType.CONTENTNODE).then(
                                            addProperty("controlType", "info.magnolia.module.form.controls.DialogStaticWithFormParams"),
                                            addProperty("label", "dialog.form.freemarkerParams.label"))),
                                getNode("tabConfirmEmail").then(
                                        addNode("freemarkerParams", ItemType.CONTENTNODE).then(
                                                addProperty("controlType", "info.magnolia.module.form.controls.DialogStaticWithFormParams"),
                                                addProperty("label", "dialog.form.freemarkerParams.label")
                                            ))))));
        
    }
}
