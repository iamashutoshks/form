/**
 * This file Copyright (c) 2014-2016 Magnolia International
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.mock.jcr.MockSession;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link MigrateValidationSelectFieldToTwinColSelectFieldTask}.
 */
public class MigrateValidationSelectFieldToTwinColSelectFieldTaskTest {

    private InstallContext ctx;
    private HierarchyManager hierarchyManager;
    private MockSession session;
    private Node root;

    @Before
    public void setUp() throws RepositoryException {
        ctx = mock(InstallContext.class);
        session = new MockSession(RepositoryConstants.CONFIG);
        root = session.getRootNode();

        hierarchyManager = mock(HierarchyManager.class);

        when(ctx.getConfigJCRSession()).thenReturn(session);
        when(ctx.getJCRSession(RepositoryConstants.CONFIG)).thenReturn(session);
        when(ctx.getHierarchyManager(RepositoryConstants.CONFIG)).thenReturn(hierarchyManager);
    }

    @Test
    public void testSuccessfulMigration() throws RepositoryException, TaskExecutionException {
        // GIVEN
        final String pathToValidationField = "/folder/testField/form/tabs/tabMain/fields/validation";

        Node field = NodeUtil.createPath(root, pathToValidationField, NodeTypes.ContentNode.NAME);
        field.setProperty("type", "String");
        field.setProperty("buttonLabel", "test");
        field.setProperty("class", "info.magnolia.ui.form.field.definition.SelectFieldDefinition");

        when(hierarchyManager.isExist(anyString())).thenReturn(true);
        when(hierarchyManager.getContent(anyString())).thenReturn(ContentUtil.asContent(field));

        MigrateValidationSelectFieldToTwinColSelectFieldTask task = new MigrateValidationSelectFieldToTwinColSelectFieldTask("", new String[]{pathToValidationField});

        // WHEN
        task.execute(ctx);

        // THEN
        assertThat(field.hasProperty("type"), is(false));
        assertThat(field.hasProperty("buttonLabel"), is(false));
        assertThat(field.hasProperty("leftColumnCaption"), is(true));
        assertThat(field.hasProperty("rightColumnCaption"), is(true));
        assertThat(field.getProperty("class").getString(), is("info.magnolia.ui.form.field.definition.TwinColSelectFieldDefinition"));
        assertThat(field.getProperty("leftColumnCaption").getString(), is("dialog.form.edit.tabMain.validation.leftColumnCaption"));
        assertThat(field.getProperty("rightColumnCaption").getString(), is("dialog.form.edit.tabMain.validation.rightColumnCaption"));
    }

    @Test
    public void testUnSuccessfulMigration() throws RepositoryException, TaskExecutionException {
        // GIVEN
        final String pathToValidationField = "/folder/testField/form/tabs/tabMain/fields/validation";

        when(hierarchyManager.isExist(anyString())).thenReturn(false);

        MigrateValidationSelectFieldToTwinColSelectFieldTask task = new MigrateValidationSelectFieldToTwinColSelectFieldTask("", new String[]{pathToValidationField});

        // WHEN
        task.execute(ctx);

        // THEN
        verify(ctx).warn(String.format(MigrateValidationSelectFieldToTwinColSelectFieldTask.WARNING_MESSAGE_FORMAT, pathToValidationField));
    }

}
