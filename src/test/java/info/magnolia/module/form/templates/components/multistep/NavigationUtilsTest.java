/**
 * This file Copyright (c) 2012-2016 Magnolia International
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

import static org.junit.Assert.*;

import info.magnolia.cms.core.MetaData;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.test.mock.jcr.MockNode;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link NavigationUtil}.
 */
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

    @Test
    public void testGetPageParagraphsOfType() throws RepositoryException {
        // GIVEN
        Node page = new MockNode();
        Node subpageShouldNotBeListed = page.addNode("subpageShouldNotBeListed");
        subpageShouldNotBeListed.addNode(MetaData.DEFAULT_META_NODE, MgnlNodeType.NT_METADATA).setProperty("mgnl:template", "form:components/formCondition");
        Node conditionList = page.addNode("content").addNode("condition").addNode("condition").addNode("condition").addNode("condition").addNode("0").addNode("conditionList");

        Node condition1 = conditionList.addNode("0");
        Node condition2 = conditionList.addNode("1");
        conditionList.addNode("wrongCondition");

        condition1.addNode(MetaData.DEFAULT_META_NODE, MgnlNodeType.NT_METADATA).setProperty("mgnl:template", "form:components/formCondition");
        condition2.addNode(MetaData.DEFAULT_META_NODE, MgnlNodeType.NT_METADATA).setProperty("mgnl:template", "form:components/formCondition");

        condition1.addNode("condition");
        condition2.addNode("condition");

        // WHEN
        List<Node> paragraphs = NodeUtil.asList(NavigationUtils.getPageParagraphsOfType(page, "form:components/formCondition"));

        // THEN
        assertFalse(paragraphs.contains(subpageShouldNotBeListed));
        assertEquals(2, paragraphs.size());
        assertEquals(condition1, paragraphs.get(0));
        assertEquals(condition2, paragraphs.get(1));
    }
}
