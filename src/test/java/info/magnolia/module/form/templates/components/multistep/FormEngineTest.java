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

import static org.mockito.Mockito.*;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.module.ModuleManagementException;
import info.magnolia.module.ModuleManager;
import info.magnolia.module.ModuleManagerImpl;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.engine.FormEngine;
import info.magnolia.module.form.engine.FormStateUtil;
import info.magnolia.module.form.engine.RedirectWithTokenAndParametersView;
import info.magnolia.module.form.engine.View;
import info.magnolia.module.form.paragraphs.models.multistep.StartStepFormEngine;
import info.magnolia.module.form.paragraphs.models.multistep.SubStepFormEngine;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.form.templates.FormStepParagraph;
import info.magnolia.module.model.ModuleDefinition;
import info.magnolia.module.model.reader.ModuleDefinitionReader;
import info.magnolia.module.templating.ParagraphManager;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockWebContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jcr.ImportUUIDBehavior;
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
    private HierarchyManager hm;
    private Content content;
    private FormParagraph configurationParagraph;
    private HttpServletResponse response;
    private HttpServletRequest request;
    private HttpSession httpSession = new DummyHttpSession();
    private MockWebContext ctx;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("form.xml");
        DataTransporter.importXmlStream(
                xmlStream,
                "website",
                "/",
                "name matters only when importing a file that needs XSL transformation",
                false,
                ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW,
                true,
                true
                );

        hm = MgnlContext.getHierarchyManager(ContentRepository.WEBSITE);
        content = hm.getContent("/multi-step-form");

        FormModule formModule = new FormModule();

        FormStepParagraph formStep = new FormStepParagraph();
        formStep.setName("formStep");

        ParagraphManager.getInstance().addParagraphToCache(formStep);

        initWebContext();

        initComponents();
    }

    private void initComponents() {
        ComponentsTestUtil.setImplementation(I18nContentSupport.class, DefaultI18nContentSupport.class);
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
    protected void initDefaultImplementations() throws IOException {
        ModuleRegistry registry = mock(ModuleRegistry.class);
        ComponentsTestUtil.setInstance(ModuleRegistry.class, registry);
        ComponentsTestUtil.setInstance(ModuleManager.class, new ModuleManagerImpl(null, new ModuleDefinitionReader() {
            public ModuleDefinition read(Reader in) throws ModuleManagementException {
                return null;
            }

            public Map readAll() throws ModuleManagementException {
                Map m = new HashMap();
                m.put("moduleDef", "dummy");
                return m;
            }

            public ModuleDefinition readFromResource(String resourcePath) throws ModuleManagementException {
                return null;
            }
        }) {
            @Override
            public List loadDefinitions() throws ModuleManagementException {
                // TODO Auto-generated method stub
                return new ArrayList();
            }
        });
        super.initDefaultImplementations();
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
        ctx.getAggregationState().setMainContent(content);
        configurationParagraph = new FormParagraph();
        configurationParagraph.setRedirectWithParams(true);
        formEngine = new StartStepFormEngine(content, configurationParagraph);

        // WHEN
        View view = formEngine.handleRequest(hm.getContent("/multi-step-form/main/singleton"));
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
        ctx.getAggregationState().setMainContent(content);
        configurationParagraph = new FormParagraph();
        configurationParagraph.setRedirectWithParams(true);
        formEngine = new StartStepFormEngine(content, configurationParagraph);

        // WHEN
        View view = formEngine.handleRequest(hm.getContent("/multi-step-form/main/singleton"));
        view.execute();

        // THEN
        assertTrue(view instanceof RedirectWithTokenAndParametersView);
        verify(response, times(1)).sendRedirect(
                "null/multi-step-form/enter-topic?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
    }

    @Test
    public void testMultiStepFormRedirectsWithParametersInUrlWhenProceedingToNextSteps() throws Exception {
        // GIVEN
        // fake that form has been submitted
        when(request.getMethod()).thenReturn("POST");
        // with parameters
        when(request.getParameterValues("name")).thenReturn(new String[] { "John Doe" });
        when(request.getParameterValues("email")).thenReturn(new String[] { "john.doe@magnolia-cms.com" });
        ctx.getAggregationState().setMainContent(content);
        configurationParagraph = new FormParagraph();
        configurationParagraph.setRedirectWithParams(true);
        formEngine = new StartStepFormEngine(content, configurationParagraph);

        // first step
        View view = formEngine.handleRequest(hm.getContent("/multi-step-form/main/singleton"));
        view.execute();

        // second step
        Map<String, String> parameters = new HashMap<String, String>(ctx.getParameters());
        // put mgnlFormToken to parameters because we need it to resolve formState
        parameters.put(FormStateUtil.FORM_TOKEN_PARAMETER_NAME, formEngine.getFormState().getToken());
        ctx.setParameters(parameters);
        when(request.getParameterValues("track")).thenReturn(new String[] { "Technical Track" });
        when(request.getParameterValues("topic")).thenReturn(new String[] { "Amazing topic" });
        when(request.getParameterValues("abstract")).thenReturn(new String[] { "Amazing abstract" });
        formEngine = new SubStepFormEngine(content, configurationParagraph, content);
        ctx.getAggregationState().setMainContent(hm.getContent("/multi-step-form/enter-topic"));
        view = formEngine.handleRequest(hm.getContent("/multi-step-form/enter-topic/main/singleton"));
        view.execute();
        ctx.getAggregationState().setMainContent(hm.getContent("/multi-step-form/enter-bio"));

        // WHEN
        // third step
        view = formEngine.handleRequest(hm.getContent("/multi-step-form/enter-bio/main/singleton"));
        view.execute();

        // THEN
        assertEquals(3, formEngine.getFormState().getSteps().values().size());
        assertTrue(view instanceof RedirectWithTokenAndParametersView);
        verify(response, times(1)).sendRedirect(
                "null/multi-step-form/enter-topic?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
        verify(response, times(1)).sendRedirect(
                "null/multi-step-form/enter-bio?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
        verify(response, times(1)).sendRedirect(
                "null/multi-step-form/upload-photo?mgnlFormToken=" + formEngine.getFormState().getToken() + "&param1=firstValue&param2=secondValue");
    }



    public static class DummyHttpSession implements HttpSession {

        Map<String, Object> attributes = new HashMap<String, Object>();

        public long getCreationTime() {
            return 0;
        }

        public String getId() {
            return null;
        }

        public long getLastAccessedTime() {
            return 0;
        }

        public ServletContext getServletContext() {
            return null;
        }

        public void setMaxInactiveInterval(int interval) {
        }

        public int getMaxInactiveInterval() {
            return 0;
        }

        public HttpSessionContext getSessionContext() {
            return null;
        }

        public Object getAttribute(String name) {
            return this.attributes.get(name);
        }

        public Object getValue(String name) {
            return null;
        }

        public Enumeration getAttributeNames() {
            return null;
        }

        public String[] getValueNames() {
            return null;
        }

        public void setAttribute(String name, Object value) {
            this.attributes.put(name, value);
        }

        public void putValue(String name, Object value) {
        }

        public void removeAttribute(String name) {
        }

        public void removeValue(String name) {
        }

        public void invalidate() {
        }

        public boolean isNew() {
            return false;
        }


    }
}
