/**
 * This file Copyright (c) 2011-2016 Magnolia International
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

import info.magnolia.migration.task.AbstractSTKRelatedModuleMigrationTask;
import info.magnolia.templatingkit.migration.util.MigrationUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;
import javax.jcr.RepositoryException;

/**
 * Custom Form Migration Task.
 *  Define an Extra Form migration task.
 */
public class FormMigrationTask extends AbstractSTKRelatedModuleMigrationTask{

    public FormMigrationTask(String taskName, String taskDescription, String moduleName, boolean disposeObservation, List<String> siteDefinition) {
        super(taskName, taskDescription, moduleName, disposeObservation, siteDefinition);
    }

    @Override
    protected void executeExtraMigrationTask(InstallContext installContext) throws TaskExecutionException {
        Session session = getSession();
        try {
            reportSystem("Starting the extra task of FormMigrationTask.");
            perform(session);
            reportSystem("Successfully executed the extra task of FormMigrationTask.");
        }catch(Exception e) {
            installContext.error("Unable to handle FormMigrationTask for the following module: "+getModuleName(), e);
            reportException(e);
            throw new TaskExecutionException("Unable to handle FormMigrationTask for the following module: "+getModuleName(), e);
        }
    }

    @Override
    protected void executeRenameAndChangeId(InstallContext installContext) throws TaskExecutionException {
        //Do nothing.
    }

    private void perform(Session session) throws RepositoryException {
        String nodePath = "/modules/form/templates/components";
        Map<String, String> componentsIdMap = getPersistentMapService().getComponentsMap();
        if(session.nodeExists(nodePath)) {
            MigrationUtil.transformForm(session, nodePath+"/form", Arrays.asList("formGroupFields"), componentsIdMap, "/form/generic/listArea.ftl", "paragraphs", "fieldsets");
            reportSystem("Transforming Form component: Remove the following node 'paragraphs' from '"+nodePath+"/form ' and create an area 'fieldsets'");
            MigrationUtil.transformForm(session, nodePath+"/formStep", Arrays.asList("formGroupFields"), componentsIdMap, "/form/generic/listArea.ftl", "areas", "fieldsets");
            reportSystem("Transforming Form component: Remove the following node 'areas' from '"+nodePath+"/formStep ' and create an area 'fieldsets'");
            MigrationUtil.transformForm(session, nodePath+"/formGroupFields", Arrays.asList("formEdit",
                "formPassword",
                "formHidden",
                "formGroupEdit",
                "formSelection",
                "formFile",
                "formSubmit",
                "formCriteria",
                "formSummary"), componentsIdMap, "/form/generic/listArea.ftl", "areas", "fields");
            reportSystem("Transforming Form component: Remove the following node 'areas' from '"+nodePath+"/formGroupFields ' and create an area 'fields'");
            MigrationUtil.transformForm(session, nodePath+"/formSubmit", Arrays.asList("formCondition"), componentsIdMap, "/form/components/conditionList.ftl", "areas", "conditionList");
            reportSystem("Transforming Form component: Remove the following node 'areas' from '"+nodePath+"/formSubmit ' and create an area 'conditionList'");
            MigrationUtil.transformForm(session, nodePath+"/formGroupEdit", Arrays.asList("formGroupEditItem"), componentsIdMap, "/form/generic/listArea.ftl", "areas", "edits");
            reportSystem("Transforming Form component: Remove the following node 'areas' from '"+nodePath+"/formGroupEdit ' and create an area 'edits'");
        }
    }
}
