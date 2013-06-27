/**
 * This file Copyright (c) 2008-2013 Magnolia International
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
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.NewPropertyTask;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.PartialBootstrapTask;
import info.magnolia.module.delta.PropertyExistsDelegateTask;
import info.magnolia.repository.RepositoryConstants;

import java.util.Arrays;

/**
 * VersionHandler for the form module.
 */
public class FormModuleVersionHandler extends DefaultModuleVersionHandler {

    public FormModuleVersionHandler() {
        register(DeltaBuilder.update("1.4", "")
            .addTask(new FormMigrationTask("Migration task: Migrate Form configuration repository", "Migrate configuration of templates, dialogs and site definitions", "form", false, Arrays.asList("")))
            );

        register(DeltaBuilder.update("1.4.4", "")
            .addTask(new NodeExistsDelegateTask("", "", RepositoryConstants.CONFIG, "/modules/form/dialogs/formGroupEdit/tabMain/controlName", new PropertyExistsDelegateTask("", "", RepositoryConstants.CONFIG, "/modules/form/dialogs/formGroupEdit/tabMain/controlName", "required", null, new NewPropertyTask("", "", RepositoryConstants.CONFIG, "/modules/form/dialogs/formGroupEdit/tabMain/controlName", "required", "true"))))
            .addTask(new NodeExistsDelegateTask("", "", RepositoryConstants.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/controlName", new PropertyExistsDelegateTask("", "", RepositoryConstants.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/controlName", "required", null, new NewPropertyTask("", "", RepositoryConstants.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/controlName", "required", "true"))))
        );

        register(DeltaBuilder.update("1.4.5", "")
            .addTask(new PartialBootstrapTask("Confirm mail type", "Bootstraps config for selecting page as type of confirm mail.", "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.form.xml", "/form/tabConfirmEmail/confirmContentType/options/page"))
            .addTask(new UpdateConfirmHtmlTypeToCodeTask("", ""))
        );

        register(DeltaBuilder.update("1.4.7", "")
                .addTask(new BootstrapSingleResource("Bootstrap formHoneypot template", "Add formHoneypot as new form component.", "/mgnl-bootstrap/form/templates/components/config.modules.form.templates.components.formHoneypot.xml"))
                .addTask(new BootstrapSingleResource("Rebootstrap formGroupFields template", "Add formHoneypot as new available component for formGroupFields.", "/mgnl-bootstrap/form/templates/components/config.modules.form.templates.components.formGroupFields.xml"))
                .addTask(new BootstrapSingleResource("Bootstrap formHoneypot dialog", "Add formHoneypot as new dialog.", "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.formHoneypot.xml"))
                .addTask(new BootstrapSingleResource("Bootstrap 'empty' validator", "Add validator for empty field.", "/mgnl-bootstrap/form/validators/config.modules.form.config.validators.empty.xml"))

                .addTask(new PartialBootstrapTask("Mail type", "Bootstraps dialog option for mail type to be sent overriding content type in the process.", "/mgnl-bootstrap/form/dialogs/config.modules.form.dialogs.form.xml", "/form/tabConfirmEmail/confirmMailType"))
                .addTask(new OrderNodeBeforeTask("Order field", "Ensure the proper order of form confirmation email dialog field.", RepositoryConstants.CONFIG, "/modules/form/dialogs/form/tabConfirmEmail/confirmMailType", "confirmContentType"))
        );
    }
}
