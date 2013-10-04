/**
 * This file Copyright (c) 2013 Magnolia International
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

import info.magnolia.ui.dialog.setup.DialogMigrationTask;
import info.magnolia.ui.dialog.setup.migration.ControlMigration;

import java.util.HashMap;

/**
 * Field Migration task for the specific Form fields.
 */
public class FormDialogMigrationTask extends DialogMigrationTask {

    public FormDialogMigrationTask(String moduleName) {
        super(moduleName);
    }

    @Override
    protected HashMap<String, ControlMigration> getCustomMigrationTask() {
        HashMap<String, ControlMigration> customMigrationTask = super.getCustomMigrationTask();
        customMigrationTask.put("info.magnolia.module.form.dialogs.DialogStaticWithFormParams", new StaticWithFormControlMigration());
        customMigrationTask.put("info.magnolia.module.form.dialogs.DialogRadioSwitch", new RadioSwitchControlMigration(customMigrationTask));
        customMigrationTask.put("info.magnolia.module.form.controls.ConditionControl", new ConditionalControlMigration());
        return customMigrationTask;
    }

}