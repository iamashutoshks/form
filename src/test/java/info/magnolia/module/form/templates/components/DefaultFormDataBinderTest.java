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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.validators.Validator;
import info.magnolia.rendering.template.TemplateAvailability;
import info.magnolia.rendering.template.configured.ConfiguredTemplateAvailability;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link DefaultFormDataBinder}.
 */
public class DefaultFormDataBinderTest extends RepositoryTestCase {

    DefaultFormDataBinder binder;
    HttpServletRequest request;
    MockWebContext ctx;
    FormStepState step;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ComponentsTestUtil.setImplementation(TemplateAvailability.class, ConfiguredTemplateAvailability.class);

        InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("form.xml");
        DataTransporter.importXmlStream(
                xmlStream,
                "website",
                "/",
                "name matters only when importing a file that needs XSL transformation",
                false,
                ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW,
                true,
                true);
        binder = new DefaultFormDataBinder();
        request = mock(HttpServletRequest.class);
        when(request.getParameterValues(anyString())).thenReturn(new String[] { "<", ">" });
        ctx = (MockWebContext) MgnlContext.getWebContext();
        ctx.setRequest(request);
        FormModule formModule = new FormModule();
        ArrayList validators= new ArrayList();
        Validator validator1 = new Validator();
        validator1.setName("test1");
        Validator validator2 = new Validator();
        validator2.setName("test2");
        validators.add(validator1);
        validators.add(validator2);
        formModule.setValidators(validators);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        MgnlContext.setInstance(null);
        ComponentsTestUtil.clear();
    }

    @Test
    public void testFieldValueHaveToBeEscaped() throws Exception {
        //GIVEN
        final String controlName = "controlName";
        Node node = new MockNode();
        node.setProperty("controlName", controlName);
        List<Node> list = new ArrayList<Node>();
        list.add(node);
        Iterator<Node> iterator = list.iterator();
        step = new FormStepState();

        //WHEN
        binder.bindAndValidateFields(iterator, step);

        //THEN
        assertEquals("&lt;__&gt;", step.get(controlName).getValue());
    }

    @Test
    public void testFieldValueIsValidatedByMultivalueValidator() throws Exception {
        //GIVEN
        final String controlName = "controlName";
        Node node = new MockNode();
        node.setProperty("controlName", controlName);
        node.setProperty("validation",new String[] {"test1","test2"});
        List<Node> list = new ArrayList<Node>();
        list.add(node);
        Iterator<Node> iterator = list.iterator();
        step = new FormStepState();

        //WHEN
        binder.bindAndValidateFields(iterator, step);

        //THEN
        assertTrue(step.isValid());
    }
}
