/**
 * This file Copyright (c) 2012 Magnolia International
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
package info.magnolia.module.form.templates.components.multistep;

import static org.junit.Assert.assertEquals;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.test.mock.jcr.MockNode;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Before;
import org.junit.Test;

public class NavigationUtilsTest {

    private static final String FIRST_CHILD = "1";
    private static final String SECOND_CHILD = "2";

    private MockNode root;
    private Node first;
    private Node second;

    @Before
    public void setUpTestStructure() throws RepositoryException {
        root = new MockNode();
        first = root.addNode(FIRST_CHILD);
        second = root.addNode(SECOND_CHILD);
    }

    @Test
    public void testGetSiblingsAfterWithType() throws RepositoryException {
        // GIVEN
        Node subFirst1 = first.addNode("subFirst1", MgnlNodeType.NT_AREA);
        Node subFirst2 = first.addNode("subFirst2", MgnlNodeType.NT_BASE);
        Node subFirst3 = first.addNode("subFirst3", MgnlNodeType.NT_BASE);
        Node subFirst4 = second.addNode("subSecond0");
        List<Node> siblings;

        // WHEN
        siblings = NavigationUtils.getSameTypeSiblingsAfter(subFirst1);
        // THEN
        assertEquals(0, siblings.size());

        // WHEN
        siblings = NavigationUtils.getSameTypeSiblingsAfter(subFirst2);
        // THEN
        assertEquals(1, siblings.size());
        assertEquals(subFirst3, siblings.get(0));

        // WHEN
        siblings = NavigationUtils.getSameTypeSiblingsAfter(subFirst3);
        // THEN
        assertEquals(0, siblings.size());

        // WHEN
        siblings = NavigationUtils.getSameTypeSiblingsAfter(subFirst4);
        // THEN
        assertEquals(0, siblings.size());
    }
}
