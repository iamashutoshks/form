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
package info.magnolia.module.form.templates.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockContent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.junit.Test;

/**
 * Tests for {@link FormSummaryModel}.
 */
public class FormSummaryModelTest extends RepositoryTestCase{

    @Test
    public void testFindAndSetComplexControlLabels() throws RepositoryException{
        //
        ComponentsTestUtil.setInstance(I18nContentSupport.class, new DefaultI18nContentSupport());
        FormSummaryModel formSummaryModel = new FormSummaryModel(mock(Node.class), mock(RenderableDefinition.class), mock(RenderingModel.class), mock(TemplatingFunctions.class), mock(RenderingContext.class));

        Content content = new MockContent("testContent");
        content.setNodeData("labels", "option1\r\noption2\r\noption3\r\noption4");

        final String controlName = "checkbox";
        Map<String, Object> stepParameters = new HashMap<String, Object>();
        stepParameters.put(controlName, "option1_option2_option4");
        Map<String, Object> templateParams = new HashMap<String, Object>();

        // WHEN
        formSummaryModel.findAndSetComplexControlLabels(content, stepParameters, templateParams, controlName);

        // THEN
        assertEquals(1, templateParams.size());
        assertTrue(templateParams.containsKey(controlName));
        assertTrue(templateParams.get(controlName) instanceof List);
        List<String> values = (List<String>) templateParams.get(controlName);
        assertEquals(3, values.size());
        assertTrue(values.contains("option1"));
        assertTrue(values.contains("option2"));
        assertTrue(values.contains("option4"));
    }

}
