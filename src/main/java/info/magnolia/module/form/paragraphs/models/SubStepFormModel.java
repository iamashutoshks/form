/**
 * This file Copyright (c) 2010-2011 Magnolia International
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
package info.magnolia.module.form.paragraphs.models;

import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.form.breadcrumb.Link;
import info.magnolia.module.form.breadcrumb.LinkImpl;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.paragraphs.models.multistep.NavigationUtils;
import info.magnolia.module.form.paragraphs.models.multistep.SubStepFormEngine;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.form.templates.FormStepParagraph;
import info.magnolia.objectfactory.Components;
import info.magnolia.registry.RegistrationException;
import info.magnolia.rendering.context.RenderingContext;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements behaviour for sub pages in multi step forms. Finds the next step by searching for the first subsequent
 * sibling that has a paragraph that uses or extends {@link info.magnolia.module.form.templates.FormStepParagraph}.
 */
public class SubStepFormModel extends AbstractFormModel {

    private static Logger log = LoggerFactory.getLogger(SubStepFormModel.class);

    public SubStepFormModel(Node content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);

    }

    @Override
    protected SubStepFormEngine createFormEngine() throws RepositoryException {

        Node startPage = Components.getComponent(RenderingContext.class).getMainContent().getParent();//MgnlContext.getAggregationState().getMainContent().getParent().getJCRNode();

        Node startParagraphNode = NavigationUtils.findParagraphOfType(startPage, FormParagraph.class);

        if (startParagraphNode == null) {
            // Ideally we would return a view that describes the problem and how to resolve it
            throw new IllegalStateException("FormStepParagraph on page [" + NodeUtil.getPathIfPossible(content) + "] could not find a FormParagraph in its parent");
        }

        String templateId = MetaDataUtil.getTemplate(startParagraphNode);
        FormParagraph startParagraph = null;
        try {
            startParagraph = (FormParagraph) Components.getComponent(TemplateDefinitionRegistry.class).getTemplateDefinition(templateId);
        } catch (RegistrationException e) {
             throw new RuntimeException(e.getMessage(), e);
        }
        return Components.newInstance(SubStepFormEngine.class, startParagraphNode, startParagraph, startPage);
    }

    public Collection<Link> getBreadcrumb() throws RepositoryException {
        List<Link> items = new ArrayList<Link>();
        Node currentPage = Components.getComponent(RenderingContext.class).getMainContent();
        Node currentStepContent = NavigationUtils.findParagraphOfType(currentPage, FormStepParagraph.class);
        boolean displayBreadcrumb = false;
        if(this.getFormState() != null) {
            Iterator<FormStepState> stepsIt = this.getFormState().getSteps().values().iterator();
            while (stepsIt.hasNext()) {
                FormStepState step = (FormStepState) stepsIt.next();
                Node stepNode = NodeUtil.getNodeByIdentifier(RepositoryConstants.WEBSITE, step.getParagraphUuid());
                if(NavigationUtils.isParagraphOfType(stepNode, FormParagraph.class)) {
                    displayBreadcrumb = PropertyUtil.getBoolean(stepNode, "displayBreadcrumb", false);
                }
                if(step.getParagraphUuid().equals(NodeUtil.getNodeIdentifierIfPossible(currentStepContent))) {
                    break;
                }
                if(displayBreadcrumb) {
                    items.add((new LinkImpl(stepNode)));
                }
            }
        }
        return items;
    }

}
