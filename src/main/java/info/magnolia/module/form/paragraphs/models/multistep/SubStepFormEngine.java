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
package info.magnolia.module.form.paragraphs.models.multistep;

import java.util.Iterator;
import javax.jcr.RepositoryException;

import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.engine.RedirectWithTokenView;
import info.magnolia.module.form.engine.View;
import info.magnolia.module.form.paragraphs.models.AbstractFormEngine;
import info.magnolia.module.form.paragraphs.models.SessionExpiredView;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.form.templates.FormStepParagraph;

/**
 * FormEngine implementation for step 2+ of multi step forms. Finds the next step by looking for the first subsequent
 * sibling that contains a paragraph of type FormStepParagraph.
 */
public class SubStepFormEngine extends AbstractFormEngine {

    private Content startPage;

    public SubStepFormEngine(Content configurationNode, FormParagraph configurationParagraph, Content startPage) {
        super(configurationNode, configurationParagraph);
        this.startPage = startPage;
    }

    @Override
    protected View handleTokenMissing() throws RepositoryException {

        // If this is an admin instance then the default behaviour of creating a new state is fine. This is needed in
        // order to let editors author the form without simultaneously filling it in.
        if (ServerConfiguration.getInstance().isAdmin())
            return super.handleTokenMissing();

        // But in a public instance we'll render a messages saying that the form starts elsewhere.
        return new GoToFirstPageView(startPage.getUUID());
    }

    @Override
    protected View handleNoSuchFormState(String formStateToken) throws RepositoryException {
        return new SessionExpiredView(startPage.getUUID());
    }

    @Override
    protected View handleNoSuchFormStateOnSubmit(String formStateToken) throws RepositoryException {
        // Send the user to the first step where he'll see an error message
        return new RedirectWithTokenView(startPage.getUUID(), formStateToken);
    }

    @Override
    protected String getNextPage() throws RepositoryException {
        // Find first sibling with step paragraph
        Iterator<Content> contentIterator = startPage.getChildren().iterator();
        NavigationUtils.advanceIteratorTilAfter(contentIterator, MgnlContext.getAggregationState().getMainContent());
        return NavigationUtils.findFirstPageWithParagraphOfType(contentIterator, FormStepParagraph.class);
    }
}
