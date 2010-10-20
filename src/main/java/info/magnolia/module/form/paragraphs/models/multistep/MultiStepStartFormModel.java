/**
 * This file Copyright (c) 2010 Magnolia International
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
package info.magnolia.module.form.paragraphs.models.multistep;

import java.util.Iterator;
import javax.jcr.RepositoryException;

import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.processing.FormProcessor;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.form.templates.FormStepParagraph;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;

/**
 * RenderingModel for the first page in a multi step form. Searches its sub pages to find the second step by looking for
 * the first paragraph that is of type or extends {@link info.magnolia.module.form.templates.FormStepParagraph}.
 */
public class MultiStepStartFormModel extends AbstractMultiStepForm {

    public MultiStepStartFormModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
    }

    @Override
    public Content getConfigurationNode() throws RepositoryException {
        return getContent();
    }

    @Override
    public String getFirstPage() throws RepositoryException {
        return MgnlContext.getAggregationState().getMainContent().getUUID();
    }

    @Override
    public String getNextPage() throws RepositoryException {
        // Find first child with step paragraph
        Content currentPage = MgnlContext.getAggregationState().getMainContent();
        Iterator<Content> contentIterator = currentPage.getChildren().iterator();
        return NavigationUtils.findFirstPageWithParagraphOfType(contentIterator, FormStepParagraph.class);
    }

    @Override
    public FormProcessor[] getProcessors() throws RepositoryException {
        return ((FormParagraph) definition).getFormProcessors();
    }
}
