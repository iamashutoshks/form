/**
 * This file Copyright (c) 2008-2009 Magnolia International
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
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.nodebuilder.task.ErrorHandling;
import info.magnolia.nodebuilder.task.ModuleNodeBuilderTask;
import static info.magnolia.nodebuilder.Ops.*;

/**
 *
 * @author tmiyar
 *
 */
public class FormModuleVersionHandler extends DefaultModuleVersionHandler {

    public FormModuleVersionHandler() {
        register(DeltaBuilder.update("1.0.4", "Update edit field styles")
            .addTask(new ArrayDelegateTask("Update gouped edit fields styles.", "",
                new CheckAndModifyPropertyValueTask("", "",
                    ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/editLength/options/s", "value", "s", "form-item-s"),
                new CheckAndModifyPropertyValueTask("", "",
                    ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/editLength/options/m", "value", "m", "form-item-m"),
                new CheckAndModifyPropertyValueTask("", "",
                    ContentRepository.CONFIG, "/modules/form/dialogs/formGroupEditItem/tabMain/editLength/options/l", "value", "l", "form-item-l")))

        );
        
        register(DeltaBuilder.update("1.1", "Adds support for hidden and password fields")
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
    }

}
