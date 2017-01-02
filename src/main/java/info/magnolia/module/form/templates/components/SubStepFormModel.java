/**
 * This file Copyright (c) 2010-2017 Magnolia International
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

import info.magnolia.cms.core.AggregationState;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.stepnavigation.Link;
import info.magnolia.module.form.stepnavigation.LinkImpl;
import info.magnolia.module.form.templates.components.multistep.NavigationUtils;
import info.magnolia.module.form.templates.components.multistep.SubStepFormEngine;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.templating.functions.TemplatingFunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Implements behaviour for sub pages in multi step forms. Finds the next step by searching for the first subsequent
 * sibling that has a paragraph that uses or extends {@link info.magnolia.module.form.templates.components.FormStepParagraph}.
 */
public class SubStepFormModel extends AbstractFormModel<RenderableDefinition> {

    protected final static String PROPERTY_HIDE_IN_STEP_NAVIGATION = "hideInStepNavigation";
    private final static String PROPERTY_DISPLAY_STEP_NAVIGATION = "displayStepNavigation";

    private final Provider<AggregationState> aggregationStateProvider;
    private final TemplateDefinitionRegistry templateDefinitionRegistry;
    private final ComponentProvider componentProvider;

    @Inject
    public SubStepFormModel(Node content, RenderableDefinition definition, RenderingModel<?> parent, TemplatingFunctions functions, Provider<AggregationState> aggregationStateProvider, TemplateDefinitionRegistry templateDefinitionRegistry, ComponentProvider componentProvider) {
        super(content, definition, parent, functions);
        this.aggregationStateProvider = aggregationStateProvider;
        this.templateDefinitionRegistry = templateDefinitionRegistry;
        this.componentProvider = componentProvider;
    }

    /**
     * @deprecated since 2.3.3. User {@link #SubStepFormModel(javax.jcr.Node, info.magnolia.rendering.template.RenderableDefinition, info.magnolia.rendering.model.RenderingModel, info.magnolia.templating.functions.TemplatingFunctions, javax.inject.Provider, info.magnolia.rendering.template.registry.TemplateDefinitionRegistry, info.magnolia.objectfactory.ComponentProvider)} instead.
     */
    @Deprecated
    public SubStepFormModel(Node content, RenderableDefinition definition, RenderingModel<?> parent, TemplatingFunctions functions) {
        this(content, definition, parent, functions, new Provider<AggregationState>() {
            @Override
            public AggregationState get() {
                return Components.getComponent(AggregationState.class);
            }
        }, Components.getComponent(TemplateDefinitionRegistry.class), Components.getComponentProvider());
    }

    @Override
    protected SubStepFormEngine createFormEngine() throws RepositoryException {

        Node startPage = aggregationStateProvider.get().getMainContentNode().getParent();

        Node startParagraphNode = NavigationUtils.findParagraphOfType(startPage, FormParagraph.class);
        if (startParagraphNode == null) {
            // Ideally we would return a view that describes the problem and how to resolve it
            throw new IllegalStateException("FormStepParagraph on page [" + NodeUtil.getPathIfPossible(content) + "] could not find a FormParagraph in its parent");
        }

        String templateId = NodeTypes.Renderable.getTemplate(startParagraphNode);
        FormParagraph startParagraph = (FormParagraph) templateDefinitionRegistry.getProvider(templateId).get();
        SubStepFormEngine subStepFormEngine = componentProvider.newInstance(SubStepFormEngine.class, startParagraphNode, startParagraph, startPage);
        //FIXME SCRUM-628: once IoC will support constructor containing several parameter with the same type we could remove the next line.
        subStepFormEngine.setStartPage(startPage);
        return subStepFormEngine;
    }

    public Collection<Link> getPreviousStepsNavigation() throws RepositoryException {
        List<Link> items = new ArrayList<>();
        Node currentPage = aggregationStateProvider.get().getMainContentNode();
        Node currentStepContent = NavigationUtils.findParagraphOfType(currentPage, FormStepParagraph.class);
        if (this.getFormState() != null) {
            Iterator<FormStepState> stepsIt = this.getFormState().getSteps().values().iterator();
            while (stepsIt.hasNext()) {
                FormStepState step = stepsIt.next();
                Node stepNode = NodeUtil.getNodeByIdentifier(getNode().getSession().getWorkspace().getName(), step.getParagraphUuid());
                if (step.getParagraphUuid().equals(NodeUtil.getNodeIdentifierIfPossible(currentStepContent))) {
                    break;
                }
                if (!PropertyUtil.getBoolean(stepNode, PROPERTY_HIDE_IN_STEP_NAVIGATION, false)) {
                    items.add((new LinkImpl(stepNode)));
                }
            }
        }
        return items;
    }

    public Collection<Link> getNextStepsNavigation() throws RepositoryException {

        List<Link> items = new ArrayList<>();
        Node currentPage = aggregationStateProvider.get().getMainContentNode();
        List<Node> list = NavigationUtils.getSameTypeSiblingsAfter(currentPage);

        for (Node stepNode : list) {
            Node currentStepContent = NavigationUtils.findParagraphOfType(currentPage, FormStepParagraph.class);
            if (currentStepContent != null) {
                Node stepNodeContent = NavigationUtils.findParagraphOfType(stepNode, FormStepParagraph.class);
                if (!PropertyUtil.getBoolean(stepNodeContent, PROPERTY_HIDE_IN_STEP_NAVIGATION, false)) {
                    items.add((new LinkImpl(stepNode)));
                }
            }
        }
        return items;
    }

    public boolean getDisplayNavigation() throws RepositoryException {

        Node currentPage = aggregationStateProvider.get().getMainContentNode();
        boolean displayStepNavigation = false;
        Node formParagraph = NavigationUtils.findParagraphOfType(currentPage, FormParagraph.class);
        if (formParagraph == null) {
            formParagraph = NavigationUtils.findParagraphOfType(currentPage.getParent(), FormParagraph.class);
        }
        if (formParagraph != null) {
            displayStepNavigation = PropertyUtil.getBoolean(formParagraph, PROPERTY_DISPLAY_STEP_NAVIGATION, false);
        }
        return displayStepNavigation;
    }
}
