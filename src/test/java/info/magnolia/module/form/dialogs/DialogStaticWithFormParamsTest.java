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
package info.magnolia.module.form.dialogs;

import static org.junit.Assert.*;

import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.jcr.MockNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.collections.IteratorUtils;
import org.junit.Test;

/**
 * Test class for {@link DialogStaticWithFormParams}.
 */
public class DialogStaticWithFormParamsTest extends RepositoryTestCase {

    @Test
    public void testFindAllFormControlNames() throws RepositoryException, IOException {
        // GIVEN
        DialogStaticWithFormParams dialog = new DialogStaticWithFormParams();

        Node contentParagraph = new MockNode();
        Node childNode = contentParagraph.addNode("firstNodeWithControlName");
        Node grandChildNode = childNode.addNode("secondNodeWithControlName");
        Node submit = contentParagraph.addNode("nodeWithFormSubmitTemplateshouldBeSkipped");
        childNode.addNode("nodeWithoutTemplate");

        childNode.setProperty("controlName", "first");
        grandChildNode.setProperty("controlName", "second");
        submit.setProperty("controlName", "unwanted");
        submit.setProperty("mgnl:template", "form:components/formHoneypot");

        // WHEN
        Iterator<Node> iterable = dialog.findAllFormControlNames(contentParagraph).iterator();

        // THEN
        List<Node> list = IteratorUtils.toList(iterable);
        assertEquals(2, list.size());
        assertFalse(list.contains(submit));
        assertEquals(childNode, list.get(0));
        assertEquals(grandChildNode, list.get(1));
    }
}
