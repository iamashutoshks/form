/**
 * This file Copyright (c) 2010-2012 Magnolia International
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.breadcrumb.Link;
import info.magnolia.module.form.breadcrumb.LinkImpl;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.paragraphs.models.multistep.NavigationUtils;
import info.magnolia.module.form.paragraphs.models.multistep.SubStepFormEngine;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.form.templates.FormStepParagraph;
import info.magnolia.module.templating.ParagraphManager;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;

/**
 * Implements behaviour for sub pages in multi step forms. Finds the next step by searching for the first subsequent
 * sibling that has a paragraph that uses or extends {@link info.magnolia.module.form.templates.FormStepParagraph}.
 */
public class SubStepFormModel extends AbstractFormModel {

    public SubStepFormModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
    }

    @Override
    protected SubStepFormEngine createFormEngine() throws RepositoryException {

        Content startPage = MgnlContext.getAggregationState().getMainContent().getParent();

        Content startParagraphNode = NavigationUtils.findParagraphOfType(startPage, FormParagraph.class);

        if (startParagraphNode == null) {
            // Ideally we would return a view that describes the problem and how to resolve it
            throw new IllegalStateException("FormStepParagraph on page [" + content.getHandle() + "] could not find a FormParagraph in its parent");
        }

        String templateName = startParagraphNode.getMetaData().getTemplate();
        FormParagraph startParagraph = (FormParagraph) ParagraphManager.getInstance().getParagraphDefinition(templateName);

        return new SubStepFormEngine(startParagraphNode, startParagraph, startPage);
    }
    
    public Collection<Link> getBreadcrumb() throws RepositoryException {
        List<Link> items = new ArrayList<Link>();
        Content currentPage = MgnlContext.getAggregationState().getMainContent();
        Content currentStepContent = NavigationUtils.findParagraphOfType(currentPage, FormStepParagraph.class);
        boolean displayBreadcrumb = false;
        if(this.getFormState() != null) {
            Iterator<FormStepState> stepsIt = this.getFormState().getSteps().values().iterator();
            while (stepsIt.hasNext()) {
                FormStepState step = (FormStepState) stepsIt.next();
                Content stepNode = ContentUtil.getContentByUUID(ContentRepository.WEBSITE, step.getParagraphUuid());
                if(NavigationUtils.isParagraphOfType(stepNode, FormParagraph.class)) {
                    displayBreadcrumb = NodeDataUtil.getBoolean(stepNode, "displayBreadcrumb", false);
                }
                if(step.getParagraphUuid().equals(currentStepContent.getUUID())) {
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
