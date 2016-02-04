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
package info.magnolia.module.form.engine;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.link.BaseLinkTest;
import info.magnolia.repository.RepositoryConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.junit.Before;
import org.junit.Test;

/**
 * Main test class for {@link FormStateUtil}.
 */
public class FormStateUtilTest extends BaseLinkTest {

    private HttpServletResponse response;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        response = new MockHttpServletResponse(mock(HttpServletResponse.class));
        when(webContext.getResponse()).thenReturn(response);

    }

    @Test
    public void sendRedirectWithTokenAndParameters() throws RepositoryException, IOException {
        // GIVEN
        String identifier = session.getNode("/parent/sub").getIdentifier();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("key", "value");

        // WHEN
        FormStateUtil.sendRedirectWithTokenAndParameters(identifier, "formExecutionToken", parameters, RepositoryConstants.WEBSITE);

        // THEN
        assertEquals("/some-context/parent/sub?mgnlFormToken=formExecutionToken&key=value", ((MockHttpServletResponse) webContext.getResponse()).getLocation());

    }

    @Test
    public void sendRedirectWithTokenAndParametersAndSpecialChar() throws RepositoryException, IOException {
        // GIVEN
        String path = "Aktivitätenkonzept";
        String identifier = session.getRootNode().addNode(path, NodeTypes.Folder.NAME).getIdentifier();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("key", "value");

        // WHEN
        FormStateUtil.sendRedirectWithTokenAndParameters(identifier, "formExecutionToken", parameters, RepositoryConstants.WEBSITE);

        // THEN
        assertEquals("/some-context/Aktivit%C3%A4tenkonzept?mgnlFormToken=formExecutionToken&key=value", ((MockHttpServletResponse) webContext.getResponse()).getLocation());

    }

    /**
     * HttpServletResponseWrapper used to retrieve the redirect location.
     */
    private class MockHttpServletResponse extends HttpServletResponseWrapper {

        private String location;

        public MockHttpServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void sendRedirect(String location) {
            this.location = location;
        }

        public String getLocation() {
            return this.location;
        }
    }

}