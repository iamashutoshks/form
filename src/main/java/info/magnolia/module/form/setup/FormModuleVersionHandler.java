/**
 * This file Copyright (c) 2008-2015 Magnolia International
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

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.ArrayDelegateTask;
import info.magnolia.module.delta.BootstrapConditionally;
import info.magnolia.module.delta.BootstrapResourcesTask;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.CheckAndModifyPropertyValueTask;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.MoveAndRenamePropertyTask;
import info.magnolia.module.delta.NewPropertyTask;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.PartialBootstrapTask;
import info.magnolia.module.delta.PropertyValueDelegateTask;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.RemovePropertyTask;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.form.setup.migration.AddMissingDefaultValuesToFieldsTask;
import info.magnolia.module.form.setup.migration.ConditionalControlMigrator;
import info.magnolia.module.form.setup.migration.RadioSwitchControlMigrator;
import info.magnolia.module.form.setup.migration.StaticWithFormControlMigrator;
import info.magnolia.module.form.templates.components.FormFieldTemplate;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.dialog.setup.DialogMigrationTask;
import info.magnolia.ui.dialog.setup.migration.ControlMigratorsRegistry;
import info.magnolia.ui.form.field.definition.StaticFieldDefinition;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * VersionHandler for the form module.
 */
public class FormModuleVersionHandler extends DefaultModuleVersionHandler {
    protected static final String PATH_VALIDATORS_EMAIL = "/modules/form/config/validators/email";
    private static final String DIALOGS_PATH = "/modules/form/dialogs/";
    private static final String COMMIT_ACTION = "/actions/commit";
    private static final String CANCEL_ACTION = "/actions/cancel";
    private static final List<String> DIALOGS = Arrays.asList(new String[] { "form", "formCondition", "formEdit", "formFile", "formGroupEdit", "formGroupEditItem", "formGroupFields", "formHidden", "formHoneypot", "formSelection", "formStep", "formSubmit", "formSummary" });

    private final Task rebootstrapBrokenDialogsTask = new ArrayDelegateTask("",

            new PropertyValueDelegateTask("Rebootstrap 'form' 'dialog' if it's broken.", "Rebootstrap 'form' 'dialog' if it's broken.", RepositoryConstants.CONFIG, "/modules/form/dialogs/form/form/tabs/tabConfirmEmail/fields/confirmContentType", "class",
                    StaticFieldDefinition.class.getCanonicalName(), true,
                    new BootstrapSingleResource("Rebootstrap 'form' dialog", "Rebootstraps 'form' dialog.", "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.form.xml"
                    )),

            new PropertyValueDelegateTask("Rebootstrap 'condition' tab into 'formCondition dialog' if broken", "Rebootstraps 'condition' tab into 'formCondition dialog' if it's broken.", RepositoryConstants.CONFIG,
                    "/modules/form/dialogs/formCondition/form/tabs/tabMain/fields/condition", "class", StaticFieldDefinition.class.getCanonicalName(), true,
                    new PartialBootstrapTask("Rebootstrap 'condition' tab into 'formCondition dialog'", "Rebootstraps 'condition' tab into 'formCondition dialog'",
                            "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.formCondition.xml", "/formCondition/form/tabs/tabMain/fields/condition"))
            );

    @Inject
    public FormModuleVersionHandler(ControlMigratorsRegistry controlMigratorsRegistry) {
        // Register control migration task.
        controlMigratorsRegistry.register("info.magnolia.module.form.dialogs.DialogStaticWithFormParams", new StaticWithFormControlMigrator());
        controlMigratorsRegistry.register("info.magnolia.module.form.dialogs.DialogRadioSwitch", new RadioSwitchControlMigrator());
        controlMigratorsRegistry.register("info.magnolia.module.form.controls.ConditionControl", new ConditionalControlMigrator());

        register(DeltaBuilder.checkPrecondition("1.4.5", "2.0"));

        register(DeltaBuilder.update("2.0", "")
                .addTask(new DialogMigrationTask("form"))
                .addTask(new BootstrapSingleResource("Bootstrap the registration of the static field definition", "", "/mgnl-bootstrap/form/config.modules.ui-framework.fieldTypes.formStaticField.xml")));

        register(DeltaBuilder.update("2.0.1", "")
                .addTask(new BootstrapSingleResource("Bootstrap formHoneypot template", "Add formHoneypot as new form component.", "/mgnl-bootstrap/form/templates/components/config.modules.form.templates.components.formHoneypot.xml"))
                .addTask(new BootstrapSingleResource("Rebootstrap formGroupFields template", "Add formHoneypot as new available component for formGroupFields.", "/mgnl-bootstrap/form/templates/components/config.modules.form.templates.components.formGroupFields.xml"))
                .addTask(new BootstrapSingleResource("Bootstrap formHoneypot dialog", "Add formHoneypot as new dialog.", "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.formHoneypot.xml"))
                .addTask(new BootstrapSingleResource("Bootstrap 'empty' validator", "Add validator for empty field.", "/mgnl-bootstrap/form/validators/config.modules.form.config.validators.empty.xml"))

                .addTask(new PartialBootstrapTask("Mail type", "Bootstraps dialog option for mail type to be sent overriding content type in the process.", "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.form.xml", "/form/form/tabs/tabConfirmEmail/fields/confirmMailType"))
                .addTask(new NodeExistsDelegateTask("Order field 'confirmMailType'", "Order field 'confirmMailType' if 'confirmContentType' field exists.", RepositoryConstants.CONFIG,
                        "/modules/form/dialogs/form/form/tabs/tabConfirmEmail/fields/confirmContentType",
                        new OrderNodeBeforeTask("Order field", "Ensure the proper order of form confirmation email dialog field.", RepositoryConstants.CONFIG,
                                "/modules/form/dialogs/form/form/tabs/tabConfirmEmail/fields/confirmMailType", "confirmContentType"))));

        DeltaBuilder for21 = DeltaBuilder.update("2.1", "");
        processDialogs(for21);
        register(for21);

        register(DeltaBuilder.update("2.2.2", "")
                .addTask(new BootstrapConditionally("Bootstrap 'formStaticField'", "Bootstrap 'formStaticField' into 'ui-framework/fieldTypes'.",
                        "/mgnl-bootstrap/form/config.modules.ui-framework.fieldTypes.formStaticField.xml"))
                .addTask(rebootstrapBrokenDialogsTask));

        register(DeltaBuilder.update("2.2.3", "")
                .addTask(new AddMissingDefaultValuesToFieldsTask(Arrays.asList(
                        "/modules/form/dialogs/form/form/tabs/tabConfirmEmail/fields/sendConfirmation/",
                        "/modules/form/dialogs/form/form/tabs/tabMain/fields/displayStepNavigation/",
                        "/modules/form/dialogs/form/form/tabs/tabSubmit/fields/trackMail/",
                        "/modules/form/dialogs/formEdit/form/tabs/tabMain/fields/mandatory/",
                        "/modules/form/dialogs/formGroupEditItem/form/tabs/tabMain/fields/mandatory",
                        "/modules/form/dialogs/formSelection/form/tabs/tabMain/fields/horizontal",
                        "/modules/form/dialogs/formSelection/form/tabs/tabMain/fields/mandatory",
                        "/modules/form/dialogs/formSelection/form/tabs/tabMain/fields/multiple",
                        "/modules/form/dialogs/formSummary/form/tabs/tabMain/fields/onlyLast"
                        ), "defaultValue", "false")
                ));

        register(DeltaBuilder.update("2.2.4", "")
                .addTask(new NodeExistsDelegateTask("Reconfigure Honeypot dialog", "Use 'HiddenFieldDefinition' for validation field in Honeypot dialog", RepositoryConstants.CONFIG, "/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", new ArrayDelegateTask("", "",
                        new SetPropertyTask(RepositoryConstants.CONFIG, "/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "class", "info.magnolia.ui.form.field.definition.HiddenFieldDefinition"),
                        new MoveAndRenamePropertyTask("Change property name 'value' to 'defaultValue' for validation field in Honeypot dialog", "/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "value", "/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "defaultValue"),
                        new RemovePropertyTask("Remove obsolate property 'buttonLabel' for validation field in Honeypot dialog", "/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "buttonLabel"),
                        new RemovePropertyTask("Remove obsolate property 'path' for validation field in Honeypot dialog", "/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "path"),
                        new RemovePropertyTask("Remove obsolate property 'repository' for validation field in Honeypot dialog", "/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "repository"))))
        );

        register(DeltaBuilder.update("2.2.5", "")
                .addTask(new NodeExistsDelegateTask("Change validators email expression data", "Change data ^\\S+@\\S+$ in /modules/form/config/validators/email/expression from  to (^$|^\\S+@\\S+$).", RepositoryConstants.CONFIG, PATH_VALIDATORS_EMAIL,
                        new CheckAndModifyPropertyValueTask(PATH_VALIDATORS_EMAIL, "expression", "^\\S+@\\S+$", "(^$|^\\S+@\\S+$)")))
                .addTask(new NodeExistsDelegateTask("Add default value to HoneyPot field", "/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", new NewPropertyTask("Add default value to HoneyPot field", "/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "defaultValue", "empty")))
        );

        register(DeltaBuilder.update("2.2.6", "")
                .addTask(new ArrayDelegateTask("Reconfigure validation fields of form field dialogs", "Change validator field to multi select field",
                        new MigrateValidationSelectFieldToTwinColSelectFieldTask("Migrate 'validation' field to multi select field",
                                new String[]{"/modules/form/dialogs/formEdit/form/tabs/tabMain/fields/validation", "/modules/form/dialogs/formGroupEditItem/form/tabs/tabMain/fields/validation"}),
                        new ChangeValidationToMultiValuedPropertyTask("", Arrays.asList("form:components/formEdit", "form:components/formPassword", "form:components/formGroupEditItem"))))
                               .addTask(new NodeExistsDelegateTask("Reconfigure FormEdit dialog", "Remove the none validator", RepositoryConstants.CONFIG, "/modules/form/config/validators/none",
                                       new RemoveNodeTask("", "/modules/form/config/validators/none")))
        );
        register(DeltaBuilder.update("2.2.7", "")
                .addTask(new NewPropertyTask("Escape Html", "Skips the default HTML escaping in password component.", RepositoryConstants.CONFIG, "/modules/form/templates/components/formPassword", "escapeHtml", false)));

        register(DeltaBuilder.update("2.2.8", "")
                .addTask(new UpdateValidatorsClassTask())
        );
        register(DeltaBuilder.update("2.2.10", "")
                .addTask(new NodeExistsDelegateTask("Change validators email expression data", "Change data (^$|^\\S+@\\S+$) in /modules/form/config/validators/email/expression from  to (^([a-zA-Z0-9_\\.\\-+])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$).", RepositoryConstants.CONFIG, PATH_VALIDATORS_EMAIL,
                        new CheckAndModifyPropertyValueTask(PATH_VALIDATORS_EMAIL, "expression", "(^$|^\\S+@\\S+$)", "(^([a-zA-Z0-9_\\.\\-+])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$)")))
        );


        DeltaBuilder for229 = DeltaBuilder.update("2.2.9", "");
        addCancelButtonTextFieldToFormSubmitTemplate(for229);
        register(for229);

        register(DeltaBuilder.update("2.2.13", "")
                .addTask(new NodeExistsDelegateTask("Add the validator 'fileUpload' to /modules/form/config/validators", "/modules/form/config/validators/fileUpload", null,
                        new BootstrapResourcesTask() {
                           @Override
                            protected String[] getResourcesToBootstrap(InstallContext installContext) {
                                return new String[]{"/mgnl-bootstrap/form/validators/config.modules.form.config.validators.fileUpload.xml"};
                                }
                            }))
                .addTask(new NodeExistsDelegateTask("Add a TwinColSelectField to the formFile dialog which allows to specify a validator to the formFile field.", "/modules/form/dialogs/formFile/form/tabs/tabMain/fields/validation", null,
                        new PartialBootstrapTask("", "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.formFile.xml", "/formFile/form/tabs/tabMain/fields/validation")))
        );

        register(DeltaBuilder.update("2.3", "")
                .addTask(new NewPropertyTask("Set template class to formPassword field", "/modules/form/templates/components/formPassword", "class", FormFieldTemplate.class.getName())));

        register(DeltaBuilder.update("2.3.2", "")
                .addTask(new ArrayDelegateTask("Bootstrap configuration for HTML5 input types support",
                        new BootstrapSingleResource("", "", "/mgnl-bootstrap/form/templates/components/config.modules.form.templates.components.formNumber.xml"),
                        new PartialBootstrapTask("", "/mgnl-bootstrap/form/templates/components/config.modules.form.templates.components.formGroupFields.xml", "formGroupFields/areas/fields/availableComponents/formNumber"),
                        new BootstrapSingleResource("", "", "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.formNumber.xml"),
                        new PartialBootstrapTask("", "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.formEdit.xml", "/formEdit/form/tabs/tabAdvanced/")
                )
        ));

        register(DeltaBuilder.update("2.3.3", "")
                .addTask(new PartialBootstrapTask("Bootstrap new hideInStepNavigation field into formStep component", "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.formStep.xml", "formStep/form/tabs/tabMain/fields/hideInStepNavigation"))
        );
    }

    private void addCancelButtonTextFieldToFormSubmitTemplate(DeltaBuilder delta) {
        final String parentPath = "/modules/form/dialogs/formSubmit/form/tabs/tabMain/fields";
        String generalDescTxt = " an additional field 'cancelButtonLabel' to" + parentPath + ".";
        PartialBootstrapTask partialBootstrapTask = new PartialBootstrapTask("Add" + generalDescTxt, "Adds" + generalDescTxt, "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.formSubmit.xml", "/formSubmit/form/tabs/tabMain/fields/cancelButtonText");
        NodeExistsDelegateTask nodeExistsDelegateTask = new NodeExistsDelegateTask("Conditionally add" + generalDescTxt + "(If " + parentPath + " exists.)", parentPath, partialBootstrapTask);
        delta.addTask(nodeExistsDelegateTask);
    }

    private void processDialogs(DeltaBuilder delta) {
        for (String dialogName : DIALOGS) {
            addLabelRemovalTasks(delta, dialogName);
        }
    }

    private void addLabelRemovalTasks(DeltaBuilder delta, String dialogName) {
        delta.addTask(new RemovePropertyTask("Remove commit action label from dialog " + dialogName, "Remove commit action label from dialog " + dialogName, RepositoryConstants.CONFIG, DIALOGS_PATH + dialogName + COMMIT_ACTION, "label"));
        delta.addTask(new RemovePropertyTask("Remove cancel action label from dialog " + dialogName, "Remove cancel action label from dialog " + dialogName, RepositoryConstants.CONFIG, DIALOGS_PATH + dialogName + CANCEL_ACTION, "label"));
    }
}