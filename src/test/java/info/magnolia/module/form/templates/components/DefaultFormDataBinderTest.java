/**
 * This file Copyright (c) 2013-2016 Magnolia International
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import info.magnolia.config.registry.DefinitionProvider;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.jcr.util.NodeTypes.Renderable;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.validators.Validator;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.mock.MockWebContext;
import info.magnolia.test.mock.jcr.MockNode;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link DefaultFormDataBinder}.
 */
public class DefaultFormDataBinderTest {

    private final static String controlNamePropertyName = "controlName";
    private final static String controlName = "theActualControlName";
    private DefaultFormDataBinder binder;
    private HttpServletRequest request;
    private MockWebContext ctx;
    private MockWebContext systemCtx;
    private FormStepState step = new FormStepState();
    private Node fieldNode;
    private List<Node> fieldList = new ArrayList<Node>();
    private Session session;
    private FormFieldTemplate formFieldTemplate = new FormFieldTemplate();
    private TemplateDefinitionRegistry templateDefinitionRegistry;
    private FormModule formModule;

    @Before
    public void setUp() throws Exception {
        templateDefinitionRegistry = mock(TemplateDefinitionRegistry.class);
        DefinitionProvider<TemplateDefinition> templateDefinitionProvider = mock(DefinitionProvider.class);
        when(templateDefinitionRegistry.getProvider(anyString())).thenReturn(templateDefinitionProvider);
        when(templateDefinitionProvider.get()).thenReturn(formFieldTemplate);

        request = mock(HttpServletRequest.class);
        session = mock(Session.class);

        ctx = new MockWebContext();
        ctx.addSession(RepositoryConstants.WEBSITE, session);

        ctx.setRequest(request);

        MgnlContext.setInstance(ctx);

        systemCtx = new MockWebContext();

        ComponentsTestUtil.setInstance(SystemContext.class, systemCtx);

        formModule = new FormModule();
        List<Validator> validators = new ArrayList<>();
        Validator validator1 = new Validator();
        validator1.setName("test1");
        Validator validator2 = new Validator();
        validator2.setName("test2");
        validators.add(validator1);
        validators.add(validator2);
        formModule.setValidators(validators);
        binder = new DefaultFormDataBinder(templateDefinitionRegistry, formModule);

        fieldNode = new MockNode();
        fieldNode.setProperty(controlNamePropertyName, controlName);

        fieldList.add(fieldNode);
    }

    @After
    public void tearDown() {
        MgnlContext.setInstance(null);
    }

    @Test
    public void testFieldValueIsEcapedByDefault() throws Exception {
        // GIVEN
        when(request.getParameterValues(anyString())).thenReturn(new String[]{"<", ">"});

        // WHEN
        binder.bindAndValidateFields(fieldList.iterator(), step);

        // THEN
        assertEquals("&lt;__&gt;", step.get(controlName).getValue());
    }

    @Test
    public void testFieldValueIsNotEscapedWhenEscapeHtmlIsFalse() throws Exception {
        // GIVEN
        fieldNode.setProperty(Renderable.TEMPLATE, "form:foo/bar");
        when(request.getParameterValues(anyString())).thenReturn(new String[] { "<", ">" });
        formFieldTemplate.setEscapeHtml(false);

        // WHEN
        binder.bindAndValidateFields(fieldList.iterator(), step);

        // THEN
        assertEquals("<__>", step.get(controlName).getValue());
    }

    @Test
    public void testFieldValueIsValidatedByMultiValuedValidator() throws Exception {
        // GIVEN
        fieldNode.setProperty("validation", new String[]{"test1", "test2"});
        when(request.getParameterValues(controlName)).thenReturn(new String[]{"anyTestValue"});

        // WHEN
        binder.bindAndValidateFields(fieldList.iterator(), step);

        // THEN
        assertTrue(step.isValid());
    }

    @Test
    public void testFieldValueIsValidatedBySingleValuedValidator() throws Exception {
        // GIVEN
        fieldNode.setProperty("validation", "test1");
        when(request.getParameterValues(controlName)).thenReturn(new String[]{"anyTestValue"});

        // WHEN
        binder.bindAndValidateFields(fieldList.iterator(), step);

        // THEN
        assertTrue(step.isValid());
    }

    @Test
    public void nonMandatoryFieldIsValidWhenItHasValidatorAndIsEmpty() throws Exception {
        // GIVEN
        Validator validator = mock(Validator.class);
        when(validator.getName()).thenReturn("validator");
        formModule.addValidators(validator);
        fieldNode.setProperty("validation", "validator");
        fieldNode.setProperty("mandatory", false);
        when(request.getParameterValues(controlName)).thenReturn(new String[] { "" });

        // WHEN
        binder.bindAndValidateFields(fieldList.iterator(), step);

        // THEN
        assertTrue(step.isValid());
    }

    @Test
    public void mandatoryFieldIsNotValidWhenItHasValidatorAndIsEmpty() throws Exception {
        // GIVEN
        Validator validator = mock(Validator.class);
        when(validator.getName()).thenReturn("validator");
        formModule.addValidators(validator);
        fieldNode.setProperty("validation", "validator");
        fieldNode.setProperty("mandatory", true);
        when(request.getParameterValues(controlName)).thenReturn(new String[] { "" });
        MockDefaultFormDataBinder binder = new MockDefaultFormDataBinder(templateDefinitionRegistry, formModule);

        // WHEN
        binder.bindAndValidateFields(fieldList.iterator(), step);

        // THEN
        assertFalse(step.isValid());
    }

    @Test
    public void mandatoryFieldsWithMultipleValuesIsNotValidWhenTheyAreEmpty() throws Exception {
        // GIVEN
        Node fieldNode1 = new MockNode();
        fieldNode1.setProperty(controlNamePropertyName, controlName);
        fieldNode1.setProperty("mandatory", true);
        fieldList.add(fieldNode1);
        fieldNode.setProperty("mandatory", true);
        when(request.getParameterValues(controlName)).thenReturn(new String[] { "", "" });
        MockDefaultFormDataBinder binder = new MockDefaultFormDataBinder(templateDefinitionRegistry, formModule);

        // WHEN
        binder.bindAndValidateFields(fieldList.iterator(), step);

        // THEN
        assertFalse(step.isValid());
    }

    private class MockDefaultFormDataBinder extends DefaultFormDataBinder {

        public MockDefaultFormDataBinder(TemplateDefinitionRegistry templateDefinitionRegistry, FormModule formModule) {
            super(templateDefinitionRegistry, formModule);
        }

        @Override
        protected String getErrorMessage(String message, Node node) {
            return "Error";
        }
    }
}
