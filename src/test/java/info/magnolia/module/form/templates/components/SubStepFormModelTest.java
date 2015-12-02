/**
 * This file Copyright (c) 2012-2015 Magnolia International
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
import static org.mockito.Mockito.*;

import info.magnolia.cms.core.AggregationState;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.security.User;
import info.magnolia.config.registry.DefinitionMetadata;
import info.magnolia.config.registry.DefinitionMetadataBuilder;
import info.magnolia.config.registry.DefinitionProvider;
import info.magnolia.config.registry.DefinitionRawView;
import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.module.form.engine.FormEngine;
import info.magnolia.module.form.engine.FormState;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.stepnavigation.Link;
import info.magnolia.module.form.templates.components.multistep.NavigationUtils;
import info.magnolia.module.form.templates.components.multistep.SubStepFormEngine;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.guice.GuiceUtils;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockSimpleComponentProvider;
import info.magnolia.test.mock.MockWebContext;

import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Provider;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link SubStepFormModel}.
 */
public class SubStepFormModelTest extends RepositoryTestCase {

    private final String templateName = "someModule:someTemplateName";
    private Session session;
    private Node formParagraph;
    private AggregationState aggregationState = new AggregationState();
    private RenderingContext renderingContext = mock(RenderingContext.class);
    private Provider<AggregationState> aggregationStateProvider = GuiceUtils.providerForInstance(aggregationState);
    private ComponentProvider componentProvider = new MockSimpleComponentProvider() {
        @Override
        public <T> T newInstance(Class<T> type, Object... parameters) {
            return (T) new SubStepFormEngine((Node) parameters[0], (FormParagraph) parameters[1], (Node) parameters[2], renderingContext);
        }
    };
    private SubStepFormModel model = new SubStepFormModel(null, null, null, null, aggregationStateProvider, this.initComponents(), componentProvider);
    private HttpServletRequest request;
    private HttpSession httpSession = new DummyHttpSession();

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
        Node content = session.getNode("/multi-step-form/upload-photo");
        Node parent = content.getParent();
        formParagraph = parent.addNode("formParagraph", NodeTypes.Component.NAME);
        NodeTypes.Renderable.set(formParagraph, templateName);

        initWebContext();
        initComponents();
        ComponentsTestUtil.setImplementation(I18nContentSupport.class, DefaultI18nContentSupport.class);
    }

    @Test
    public void testNextStepsNavigation() throws RepositoryException {
        //GIVEN
        Collection<Link> nextSteps;
        setStepNavigation(true);

        //WHEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(0, nextSteps.size());

        //WHEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form/enter-topic"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(3, nextSteps.size());

        //WHEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form/enter-bio"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(2, nextSteps.size());

        //WHEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form/upload-photo"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(1, nextSteps.size());

        //WHEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form/thanks"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(0, nextSteps.size());
    }

    @Test
    public void testNextStepsNavigationHiddenItems() throws RepositoryException {
        //GIVEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form/upload-photo"));
        setStepNavigation(true);
        Collection<Link> nextSteps = model.getNextStepsNavigation();
        assertEquals(1, nextSteps.size());

        //WHEN
        session.getNode("/multi-step-form/thanks").setProperty(SubStepFormModel.PROPERTY_HIDE_IN_STEP_NAVIGATION, true);
        nextSteps = model.getNextStepsNavigation();

        //THEN
        assertEquals(0, nextSteps.size());
    }

    @Test
    public void testGetPreviousStepsNavigation() throws RepositoryException {
        //GIVEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form/upload-photo"));
        when(renderingContext.getMainContent()).thenReturn(aggregationState.getMainContentNode());
        when(request.getMethod()).thenReturn("GET");

        FormState formState = new FormState();
        FormStepState formStepState = new FormStepState();
        formStepState.setParagraphUuid(formParagraph.getIdentifier());
        formState.addStep(formStepState);
        MgnlContext.getParameters().put("mgnlFormToken", aggregationState.getMainContentNode().getIdentifier());
        httpSession.setAttribute(FormEngine.class.getName() + "-formState-" + aggregationState.getMainContentNode().getIdentifier(), formState);

        model = new SubStepFormModel(aggregationState.getMainContentNode(), null, null, null, aggregationStateProvider, this.initComponents(), componentProvider);
        model.execute();

        setStepNavigation(true);

        //WHEN
        Collection<Link> nextSteps = model.getPreviousStepsNavigation();
        //THEN
        assertEquals(1, nextSteps.size());

        //WHEN
        formParagraph.setProperty(SubStepFormModel.PROPERTY_HIDE_IN_STEP_NAVIGATION, true);
        nextSteps = model.getPreviousStepsNavigation();
        //THEN
        assertEquals(0, nextSteps.size());
    }

    @Test
    public void testNextStepsNavigationWrongNode() throws Exception {
        //GIVEN
        Collection<Link> nextSteps;

        //WHEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form"));
        nextSteps = model.getNextStepsNavigation();
        //THEN
        assertEquals(0, nextSteps.size());
    }

    @Test
    public void testGetDisplayNavigation() throws RepositoryException {
        //GIVEN
        Boolean displayNavigation;

        //GIVEN
        //navigation isn't set
        //WHEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form"));
        displayNavigation = model.getDisplayNavigation();
        //THEN
        assertFalse(displayNavigation);

        //GIVEN
        setStepNavigation(true);
        //WHEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form/enter-topic"));
        displayNavigation = model.getDisplayNavigation();
        //THEN
        assertTrue(displayNavigation);

        //GIVEN
        setStepNavigation(false);
        //WHEN
        aggregationState.setMainContentNode(session.getNode("/multi-step-form"));
        displayNavigation = model.getDisplayNavigation();
        //THEN
        assertFalse(displayNavigation);
    }

    private void setStepNavigation(boolean value) throws RepositoryException {
        Node currentPage = session.getNode("/multi-step-form");
        Node formParagraph = NavigationUtils.findParagraphOfType(currentPage, FormParagraph.class);
        formParagraph.setProperty("displayStepNavigation", value);
    }

    private TemplateDefinitionRegistry initComponents() {
        TemplateDefinitionRegistry templateDefinitionRegistry = new TemplateDefinitionRegistry();
        TemplateDefinition definition = new ConfiguredTemplateDefinition(null);

        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formStep", new FormStepParagraph(null)));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formGroupFields", definition));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formSelection", definition));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formEdit", definition));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/form", definition));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("form:components/formSubmit", definition));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider(templateName, new FormParagraph(null)));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("standard-templating-kit:pages/stkFormStep", new FormStepParagraph(null)));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("standard-templating-kit:pages/stkArticle", definition));
        templateDefinitionRegistry.register(new DummyTemplateDefinitionProvider("standard-templating-kit:components/content/stkTextImage", definition));

        ComponentsTestUtil.setInstance(TemplateDefinitionRegistry.class, templateDefinitionRegistry);
        return templateDefinitionRegistry;
    }

    private void initWebContext() {
        request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        MockWebContext ctx = (MockWebContext) MgnlContext.getInstance();
        Map<String, String> parameters = new HashMap<String, String>();
        ctx.setParameters(parameters);
        ctx.setRequest(request);
        ctx.setResponse(response);
        ctx.setLocale(new Locale("en"));
        ctx.setUser(mock(User.class));
        MgnlContext.setInstance(ctx);

        when(request.getSession()).thenReturn(httpSession);
        when(request.getQueryString()).thenReturn("param1=firstValue&param2=secondValue");
        when(request.getMethod()).thenReturn("POST");
    }

    public static class DummyTemplateDefinitionProvider implements DefinitionProvider<TemplateDefinition> {

        private final String id;
        private final TemplateDefinition templateDefinition;

        public DummyTemplateDefinitionProvider(String id, TemplateDefinition templateDefinition) {
            this.id = id;
            this.templateDefinition = templateDefinition;
        }

        @Override
        public TemplateDefinition get() {
            return this.templateDefinition;
        }

        @Override
        public DefinitionMetadata getMetadata() {
            return new DefinitionMetadataBuilder.DefinitionMetadataImpl(null, id, null, null, null, null);
        }

        @Override
        public DefinitionRawView getRaw() {
            return null;
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public List<String> getErrorMessages() {
            return null;
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
