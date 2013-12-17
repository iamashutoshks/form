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
package info.magnolia.module.form.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.PartialBootstrapTask;
import info.magnolia.module.delta.RemovePropertyTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.form.setup.migration.ConditionalControlMigrator;
import info.magnolia.module.form.setup.migration.RadioSwitchControlMigrator;
import info.magnolia.module.form.setup.migration.StaticWithFormControlMigrator;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.dialog.setup.DialogMigrationTask;
import info.magnolia.ui.dialog.setup.migration.ControlMigratorsRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * VersionHandler for the form module.
 */
public class FormModuleVersionHandler extends DefaultModuleVersionHandler {

    private static final String DIALOGS_PATH = "/modules/form/dialogs/";
    private static final String COMMIT_ACTION = "/actions/commit";
    private static final String CANCEL_ACTION = "/actions/cancel";
    private static final List<String> DIALOGS = Arrays.asList(new String[] { "form", "formCondition", "formEdit", "formFile", "formGroupEdit", "formGroupEditItem", "formGroupFields", "formHidden", "formHoneypot", "formSelection", "formStep", "formSubmit", "formSummary" });

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
                .addTask(new OrderNodeBeforeTask("Order field", "Ensure the proper order of form confirmation email dialog field.", RepositoryConstants.CONFIG, "/modules/form/dialogs/form/form/tabs/tabConfirmEmail/fields/confirmMailType", "confirmContentType"))
        );

        DeltaBuilder for21 = DeltaBuilder.update("2.1", "");
        processDialogs(for21);
        register(for21);

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

    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        final List<Task> tasks = new ArrayList<Task>();
        return tasks;
    }
}
