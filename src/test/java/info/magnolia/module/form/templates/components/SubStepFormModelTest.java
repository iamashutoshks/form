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
package info.magnolia.module.form.templates.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.core.MetaData;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.module.form.stepnavigation.Link;
import info.magnolia.module.form.templates.components.multistep.NavigationUtils;
import info.magnolia.registry.RegistrationException;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionProvider;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockWebContext;

import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.junit.Before;
import org.junit.Test;

public class SubStepFormModelTest extends RepositoryTestCase {

    private Session session;
    private Node content;
    private RenderingContext renderingContext;
    private FormParagraph configurationParagraph;
    private HttpServletResponse response;
    private HttpServletRequest request;
    private final HttpSession httpSession = new DummyHttpSession();
    private MockWebContext ctx;
    private final String templateName = "someTemplateName";
    private final String formStepNode = "/multi-step-form/upload-photo";

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
                true);

        session = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        content = session.getNode(formStepNode);
        Node parent = content.getParent();
        Node child = parent.addNode("formParagraph", MgnlNodeType.NT_COMPONENT);
        Node metaData = child.getNode(MetaData.DEFAULT_META_NODE);
        metaData.setProperty("mgnl:template", templateName);

        renderingContext = mock(RenderingContext.class);
        ComponentsTestUtil.setInstance(RenderingContext.class, renderingContext);

        initWebContext();
        initComponents();

        when(request.getMethod()).thenReturn("POST");
    }

    @Test
    public void testNextStepsNavigation() throws RepositoryException {
        //GIVEN
        SubStepFormModel model = new SubStepFormModel(content, configurationParagraph, null, null);
        Collection<Link> nextSteps;
        setStepNavigation(true);

        //WHEN
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(0, nextSteps.size());

        //WHEN
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form/enter-topic"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(3, nextSteps.size());

        //WHEN
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form/enter-bio"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(2, nextSteps.size());

        //WHEN
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form/upload-photo"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(1, nextSteps.size());

        //WHEN
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form/thanks"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(0, nextSteps.size());
    }

    @Test
    public void testNextStepsNavigationWrongNode() throws PathNotFoundException, RepositoryException {
        //GIVEN
        SubStepFormModel model = new SubStepFormModel(content, configurationParagraph, null, null);
        Collection<Link> nextSteps;

        //WHEN
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(0, nextSteps.size());
    }

    public void testGetDisplayNavigation() throws RepositoryException {
        //GIVEN
        SubStepFormModel model = new SubStepFormModel(content, configurationParagraph, null, null);
        Boolean displayNavigation;

        //GIVEN
        //navigation isn't set
        //WHEN
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form"));
        displayNavigation = model.getDisplayNavigation();
        //THEN
        assertFalse(displayNavigation);

        //GIVEN
        setStepNavigation(true);
        //WHEN
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form/enter-topic"));
        displayNavigation = model.getDisplayNavigation();
        //THEN
        assertTrue(displayNavigation);

        //GIVEN
        setStepNavigation(false);
        //WHEN
        when(renderingContext.getMainContent()).thenReturn(session.getNode("/multi-step-form"));
        displayNavigation = model.getDisplayNavigation();
        //THEN
        assertFalse(displayNavigation);
    }

    private void setStepNavigation(boolean value) throws PathNotFoundException, ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException {
        Node currentPage = session.getNode("/multi-step-form");
        Node formParagraph = NavigationUtils.findParagraphOfType(currentPage, FormParagraph.class);
        formParagraph.setProperty("displayStepNavigation", value);
    }

    private void initComponents() {
        TemplateDefinitionRegistry templateDefinitionRegistry = new TemplateDefinitionRegistry();
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formStep", new FormStepParagraph()));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formGroupFields", null));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formSelection", null));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formEdit", null));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/form", null));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formSubmit", null));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider(templateName, new FormParagraph()));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("standard-templating-kit:pages/stkFormStep", new FormStepParagraph()));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("standard-templating-kit:pages/stkArticle", null));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("standard-templating-kit:components/content/stkTextImage", null));

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

    public static class DummyTemplateDefinitionProvider implements TemplateDefinitionProvider {

        private final String id;
        private final TemplateDefinition templateDefinition;

        public DummyTemplateDefinitionProvider(String id, TemplateDefinition templateDefinition) {
            this.id = id;
            this.templateDefinition = templateDefinition;
//            templateDefinition.setId("someTemplate");
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
