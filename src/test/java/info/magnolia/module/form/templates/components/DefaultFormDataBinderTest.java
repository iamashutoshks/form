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
package info.magnolia.module.form.templates.components;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.test.MgnlTestCase;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

/**
 * Tests for {@link DefaultFormDataBinder}.
 */
public class DefaultFormDataBinderTest extends MgnlTestCase {

    @Test
    public void testFieldValueHaveToBeEscaped() throws Exception {
        // GIVEN
        final String controlName = "controlName";
        Node node = new MockNode();
        node.setProperty("controlName", controlName);
        List<Node> list = new ArrayList<Node>();
        list.add(node);
        Iterator<Node> iterator = list.iterator();
        FormStepState step = new FormStepState();

        DefaultFormDataBinder binder = new DefaultFormDataBinder();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterValues(anyString())).thenReturn(new String[] { "<", ">" });
        MockWebContext ctx = (MockWebContext) MgnlContext.getWebContext();
        ctx.setRequest(request);

        // WHEN
        binder.bindAndValidateFields(iterator, step);

        // THEN
        assertEquals("&lt;__&gt;", step.get(controlName).getValue());
    }
}
