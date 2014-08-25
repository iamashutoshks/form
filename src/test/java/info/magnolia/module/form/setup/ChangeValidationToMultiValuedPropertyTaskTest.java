/**
 * This file Copyright (c) 2014 Magnolia International
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockSession;

import java.util.ArrayList;
import java.util.Arrays;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link ChangeValidationToMultiValuedPropertyTask}.
 */
public class ChangeValidationToMultiValuedPropertyTaskTest {
    MockWebContext ctx;
    Session websiteSession;
    InstallContext installContext;

   @Before
    public void setUp() throws Exception {
        ctx = new MockWebContext();
        MgnlContext.setInstance(ctx);
        websiteSession = new MockSession(RepositoryConstants.WEBSITE);
        ctx.addSession(RepositoryConstants.WEBSITE, websiteSession);
        installContext = mock(InstallContext.class);
        when(installContext.getJCRSession(RepositoryConstants.WEBSITE)).thenReturn(websiteSession);
    }

    @After
    public void tearDown() throws Exception {
       MgnlContext.setInstance(null);
    }

    @Test
    public void testChangeValidationToMultiValueOnlyForNodeWithTemplateInTemplateList() throws TaskExecutionException, RepositoryException {
        // GIVEN
        ChangeValidationToMultiValuedPropertyTask changeValidationToMultiValuedPropertyTask = new ChangeValidationToMultiValuedPropertyTask("", new ArrayList<String>(Arrays.asList("form:components/formEdit")));
        Node validationWebsiteNode = NodeUtil.createPath(websiteSession.getRootNode(), "fields/0", NodeTypes.ContentNode.NAME);
        validationWebsiteNode.setProperty("validation", "email");
        validationWebsiteNode.setProperty("mgnl:template", "form:components/formEdit");
        Node validationWebsiteNode2 = NodeUtil.createPath(websiteSession.getRootNode(), "fields/1", NodeTypes.ContentNode.NAME);
        validationWebsiteNode2.setProperty("validation", "email");
        validationWebsiteNode2.setProperty("mgnl:template", "form:components/test");

        // WHEN
        changeValidationToMultiValuedPropertyTask.execute(installContext);

        // THEN
        assertTrue(validationWebsiteNode.getProperty("validation").isMultiple());
        assertFalse(validationWebsiteNode2.getProperty("validation").isMultiple());
    }
}
