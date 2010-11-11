/**
 * This file Copyright (c) 2010 Magnolia International
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

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

import javax.jcr.RepositoryException;
import java.util.Collection;

/**
 * Visits all dialog definitions in form module.
 * 
 * @author ochytil
 * @version $Revision: $ ($Author: $)
 */
public abstract class UpdateAllDialogDefinitions extends AbstractRepositoryTask {

    public UpdateAllDialogDefinitions(String name, String description) {
        super(name, description);
    }

    @Override
    protected void doExecute(InstallContext ctx) throws RepositoryException, TaskExecutionException {
        Content modulesNode = ctx.getModulesNode();

            Content dialogsNode = modulesNode.getContent("form").getContent("dialogs");
            Collection<Content> dialogDefinitions = dialogsNode.getChildren(ItemType.CONTENTNODE);

            for (Content dialogDefinition : dialogDefinitions) {
                updateDialogDefinition(ctx, dialogDefinition);
            }
    }

    protected abstract void updateDialogDefinition(InstallContext ctx, Content siteDefinition) throws RepositoryException, TaskExecutionException;
}