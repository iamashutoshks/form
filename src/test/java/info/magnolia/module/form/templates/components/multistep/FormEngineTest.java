/**
 * This file Copyright (c) 2012-2017 Magnolia International
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
import static org.mockito.Mockito.*;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.i18n.AbstractI18nContentSupport;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.i18n.LocaleDefinition;
import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.engine.FormEngine;
import info.magnolia.module.form.engine.FormStateUtil;
import info.magnolia.module.form.engine.RedirectWithTokenAndParametersView;
import info.magnolia.module.form.engine.RedirectWithTokenView;
import info.magnolia.module.form.engine.View;
import info.magnolia.module.form.templates.components.FormParagraph;
import info.magnolia.module.form.templates.components.FormStepParagraph;
import info.magnolia.registry.RegistrationException;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.template.TemplateAvailability;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.configured.ConfiguredTemplateAvailability;
import info.magnolia.rendering.template.registry.TemplateDefinitionProvider;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockWebContext;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for FormEngine.
 */
public class FormEngineTest extends RepositoryTestCase {

    private FormEngine formEngine;
    private Session session;
    private Node content;
    private RenderingContext renderingContext;
    private FormParagraph configurationParagraph;
    private HttpServletResponse response;
    private HttpServletRequest request;
    private final HttpSession httpSession = new DummyHttpSession();
    private MockWebContext ctx;

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

        session = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        content = session.getNode("/multi-step-form");

        renderingContext = mock(RenderingContext.class);
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form"));

        FormModule formModule = new FormModule();

        initWebContext();

        initComponents();
    }

    private void initComponents() {
        TemplateDefinitionRegistry templateDefinitionRegistry = new TemplateDefinitionRegistry();
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formStep", new FormStepParagraph()));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formGroupFields", null));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formSelection", null));

        ComponentsTestUtil.setImplementation(I18nContentSupport.class, DefaultI18nContentSupport.class);
        ComponentsTestUtil.setImplementation(ServerConfiguration.class, ServerConfiguration.class);
        ComponentsTestUtil.setInstance(TemplateDefinitionRegistry.class, templateDefinitionRegistry);
    }

    private void initWebContext() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);

        ctx = (MockWebContext) MgnlContext.getInstance();
        Map<String, String> parameters = new HashMap<String, String>();
        ctx.setParameters(parameters);
        ctx.setRequest(request);
        ctx.setResponse(response);
        ctx.setLocale(new Locale("en"));

        when(request.getSession()).thenReturn(httpSession);
        when(request.getQueryString()).thenReturn("param1=firstValue&param2=secondValue");
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        MgnlContext.setInstance(null);
        ComponentsTestUtil.clear();
    }

    @Test
    public void testFormRedirectsWithParametersInUrlWhenSubmittedAndValidationFailed() throws Exception {
        // GIVEN
        // fake that form has been submitted
        when(request.getMethod()).thenReturn("POST");

        configurationParagraph = new FormParagraph();
        configurationParagraph.setRedirectWithParams(true);
        formEngine = new StartStepFormEngine(content, configurationParagraph, renderingContext);

        // WHEN
        View view = formEngine.handleRequest(session.getNode("/multi-step-form/content/singleton"));
        view.execute();

        // THEN
        assertTrue(view instanceof RedirectWithTokenAndParametersView);
        verify(response, times(1)).sendRedirect(
                "null/multi-step-form?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
    }

    @Test
    public void testFormRedirectsWithParametersInUrlWhenSubmittedAndValidatedSuccessfully() throws Exception {
        // GIVEN
        // fake that form has been submitted
        when(request.getMethod()).thenReturn("POST");
        // with parameters
        when(request.getParameterValues("name")).thenReturn(new String[] { "John Doe" });
        when(request.getParameterValues("email")).thenReturn(new String[] { "john.doe@magnolia-cms.com" });

        configurationParagraph = new FormParagraph();
        configurationParagraph.setRedirectWithParams(true);
        formEngine = new StartStepFormEngine(content, configurationParagraph, renderingContext);

        // WHEN
        View view = formEngine.handleRequest(session.getNode("/multi-step-form/content/singleton"));
        view.execute();

        // THEN
        assertTrue(view instanceof RedirectWithTokenAndParametersView);
        verify(response, times(1)).sendRedirect(
                "null/multi-step-form/enter-topic?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
    }

    @Test
    public void testFormRedirectsWithParametersInUrlWhenBackButtonClicked() throws Exception {
        // GIVEN
        // fake that form has been submitted
        when(request.getMethod()).thenReturn("POST");
        // with parameters
        when(request.getParameterValues("name")).thenReturn(new String[] { "John Doe" });
        when(request.getParameterValues("email")).thenReturn(new String[] { "john.doe@magnolia-cms.com" });

        configurationParagraph = new FormParagraph();
        configurationParagraph.setRedirectWithParams(true);
        formEngine = new StartStepFormEngine(content, configurationParagraph, renderingContext);
        // process first step
        formEngine.handleRequest(session.getNode("/multi-step-form/content/singleton"));

        // now we are in step two
        Map<String, String> parameters = new HashMap<String, String>(ctx.getParameters());
        // put mgnlFormToken to parameters because we need it to resolve formState
        parameters.put(FormStateUtil.FORM_TOKEN_PARAMETER_NAME, formEngine.getFormState().getToken());
        ctx.setParameters(parameters);
        // fake back button was pressed
        when(request.getParameter("mgnlFormBackButtonPressed")).thenReturn("true");

        // WHEN
        View view = formEngine.handleRequest(session.getNode("/multi-step-form/enter-topic"));
        view.execute();

        // THEN
        assertTrue(view instanceof RedirectWithTokenAndParametersView);
        verify(response, times(1)).sendRedirect(
                "null/multi-step-form?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
    }

    @Test
    public void testMultiStepFormRedirectsWithParametersInUrlWhenProceedingToNextSteps() throws Exception {
        // GIVEN
        // fake that form has been submitted
        when(request.getMethod()).thenReturn("POST");
        // with parameters
        when(request.getParameterValues("name")).thenReturn(new String[] { "John Doe" });
        when(request.getParameterValues("email")).thenReturn(new String[] { "john.doe@magnolia-cms.com" });

        configurationParagraph = new FormParagraph();
        configurationParagraph.setRedirectWithParams(true);
        formEngine = new StartStepFormEngine(content, configurationParagraph, renderingContext);

        // first step
        View view = formEngine.handleRequest(session.getNode("/multi-step-form/content/singleton"));
        view.execute();

        // second step
        Map<String, String> parameters = new HashMap<String, String>(ctx.getParameters());
        // put mgnlFormToken to parameters because we need it to resolve formState
        parameters.put(FormStateUtil.FORM_TOKEN_PARAMETER_NAME, formEngine.getFormState().getToken());
        ctx.setParameters(parameters);
        when(request.getParameterValues("track")).thenReturn(new String[] { "Technical Track" });
        when(request.getParameterValues("topic")).thenReturn(new String[] { "Amazing topic" });
        when(request.getParameterValues("abstract")).thenReturn(new String[] { "Amazing abstract" });
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form/enter-topic"));
        formEngine = new SubStepFormEngine(content, configurationParagraph, content, renderingContext);
        view = formEngine.handleRequest(session.getNode("/multi-step-form/enter-topic/content/singleton"));
        view.execute();

        // WHEN
        // third step
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form/enter-bio"));
        view = formEngine.handleRequest(session.getNode("/multi-step-form/enter-bio/content/singleton"));
        view.execute();

        // THEN
        assertEquals(3, formEngine.getFormState().getCurrentlyExecutingStep());
        assertTrue(view instanceof RedirectWithTokenAndParametersView);
        verify(response, times(1)).sendRedirect(
                "null/multi-step-form/enter-topic?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
        verify(response, times(1)).sendRedirect(
                "null/multi-step-form/enter-bio?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
        verify(response, times(1)).sendRedirect(
                "null/multi-step-form/upload-photo?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
    }

    @Test
    public void testI18nRedirectWithTokenAndParametersView() throws Exception {
        // GIVEN
        Locale locale = new Locale("de");
        LocaleDefinition definition = new LocaleDefinition();
        definition.setLocale(locale);
        definition.setEnabled(true);
        // locale
        AbstractI18nContentSupport support = ((AbstractI18nContentSupport) I18nContentSupportFactory.getI18nSupport());
        support.setEnabled(true);
        support.addLocale(definition);
        MgnlContext.setLocale(locale);
        when(request.getMethod()).thenReturn("POST");

        configurationParagraph = new FormParagraph();
        configurationParagraph.setRedirectWithParams(true);
        formEngine = new StartStepFormEngine(content, configurationParagraph, renderingContext);

        // WHEN
        View view = formEngine.handleRequest(session.getNode("/multi-step-form/content/singleton"));
        view.execute();

        // THEN
        assertTrue(view instanceof RedirectWithTokenAndParametersView);
        verify(response, times(1)).sendRedirect("null/de/multi-step-form?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
    }

    @Test
    public void testI18nRedirectWithTokenView() throws Exception {
        // GIVEN
        Locale locale = new Locale("de");
        LocaleDefinition definition = new LocaleDefinition();
        definition.setLocale(locale);
        definition.setEnabled(true);
        // locale
        AbstractI18nContentSupport support = ((AbstractI18nContentSupport) I18nContentSupportFactory.getI18nSupport());
        support.setEnabled(true);
        support.addLocale(definition);
        MgnlContext.setLocale(locale);
        when(request.getMethod()).thenReturn("POST");

        configurationParagraph = new FormParagraph();
        formEngine = new StartStepFormEngine(content, configurationParagraph, renderingContext);

        // WHEN
        View view = formEngine.handleRequest(session.getNode("/multi-step-form/content/singleton"));
        view.execute();

        // THEN
        assertTrue(view instanceof RedirectWithTokenView);
        verify(response, times(1)).sendRedirect("null/de/multi-step-form?mgnlFormToken=" + formEngine.getFormState().getToken());
    }

    public static class DummyTemplateDefinitionProvider implements TemplateDefinitionProvider {

        private final String id;
        private final TemplateDefinition templateDefinition;

        public DummyTemplateDefinitionProvider(String id, TemplateDefinition templateDefinition) {
            this.id = id;
            this.templateDefinition = templateDefinition;
        }

        @Override
        public String getId() {
            return this.id;
        }

        @Override
        public TemplateDefinition getTemplateDefinition() throws RegistrationException {
            return this.templateDefinition;
        }

    }

    public static class DummyHttpSession implements HttpSession {

        private final Map<String, Object> attributes = new HashMap<String, Object>();

        @Override
        public long getCreationTime() {
            return 0;
        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public long getLastAccessedTime() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public void setMaxInactiveInterval(int interval) {
        }

        @Override
        public int getMaxInactiveInterval() {
            return 0;
        }

        @Override
        public HttpSessionContext getSessionContext() {
            return null;
        }

        @Override
        public Object getAttribute(String name) {
            return this.attributes.get(name);
        }

        @Override
        public Object getValue(String name) {
            return null;
        }

        @Override
        public Enumeration getAttributeNames() {
            return null;
        }

        @Override
        public String[] getValueNames() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object value) {
            this.attributes.put(name, value);
        }

        @Override
        public void putValue(String name, Object value) {
        }

        @Override
        public void removeAttribute(String name) {
        }

        @Override
        public void removeValue(String name) {
        }

        @Override
        public void invalidate() {
        }

        @Override
        public boolean isNew() {
            return false;
        }

    }
}
