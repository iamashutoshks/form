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

import org.apache.commons.lang.StringUtils;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.engine.RedirectWithTokenView;
import info.magnolia.module.form.engine.View;
import info.magnolia.module.form.processors.FormProcessor;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.form.templates.FormStepParagraph;
import info.magnolia.module.templating.ParagraphManager;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;

/**
 * Implements behaviour for sub pages in multi step forms. Finds the next step by searching for the first subsequent
 * sibling that has a paragraph that uses or extends {@link info.magnolia.module.form.templates.FormStepParagraph}.
 */
public class MultiStepSubPageFormModel extends AbstractMultiStepForm {

    public MultiStepSubPageFormModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
    }

    @Override
    protected View handleTokenMissing() throws RepositoryException {

        // If this is an admin instance then the default behaviour of creating a new state is fine. This is needed in
        // order to let editors author the form without simultaneously filling it in.
        if (ServerConfiguration.getInstance().isAdmin())
            return super.handleTokenMissing();

        // But in a public instance we'll render a messages saying that the form starts elsewhere.
        return new GoToFirstPageView(getFirstPage());
    }

    @Override
    protected View handleNoSuchFormState(String formStateToken) throws RepositoryException {
        return new SessionExpiredView(getFirstPage());
    }

    @Override
    protected View handleNoSuchFormStateOnSubmit(String formStateToken) throws RepositoryException {
        // Send the user to the first step where he'll see an error message
        return new RedirectWithTokenView(getFirstPage(), formStateToken);
    }

    @Override
    public Content getConfigurationNode() throws RepositoryException {
        return getStartParagraphNode();
    }

    @Override
    public String getFirstPage() throws RepositoryException {
        return getStartPage().getUUID();
    }

    @Override
    public String getNextPage() throws RepositoryException {
        // Find first sibling with step paragraph
        Iterator<Content> contentIterator = getStartPage().getChildren().iterator();
        NavigationUtils.advanceIteratorTilAfter(contentIterator, MgnlContext.getAggregationState().getMainContent());
        return NavigationUtils.findFirstPageWithParagraphOfType(contentIterator, FormStepParagraph.class);
    }

    @Override
    public FormProcessor[] getProcessors() throws RepositoryException {
        return getStartParagraph().getFormProcessors();
    }

    // Called from templates and models of inner paragraphs

    @Override
    public String getParagraphsAsStringList() throws RepositoryException {
        return StringUtils.join((getStartParagraph()).getParagraphs(), ", ");
    }

    // Private utility methods

    private Content getStartPage() throws RepositoryException {
        return MgnlContext.getAggregationState().getMainContent().getParent();
    }

    private FormParagraph getStartParagraph() throws RepositoryException {
        String templateName = getStartParagraphNode().getMetaData().getTemplate();
        return (FormParagraph) ParagraphManager.getInstance().getParagraphDefinition(templateName);
    }

    private Content getStartParagraphNode() throws RepositoryException {
        return NavigationUtils.findParagraphOfType(getStartPage(), FormParagraph.class);
    }
}
