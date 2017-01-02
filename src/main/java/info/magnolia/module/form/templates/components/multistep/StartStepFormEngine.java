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
package info.magnolia.module.form.templates.components.multistep;

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.form.engine.FormState;
import info.magnolia.module.form.engine.FormStateTokenMissingException;
import info.magnolia.module.form.engine.FormStateUtil;
import info.magnolia.module.form.engine.NoSuchFormStateException;
import info.magnolia.module.form.templates.components.AbstractFormEngine;
import info.magnolia.module.form.templates.components.FormParagraph;
import info.magnolia.module.form.templates.components.FormStepParagraph;
import info.magnolia.rendering.context.RenderingContext;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 * FormEngine implementation for the first step of a multi step form, or a single step form.
 */
public class StartStepFormEngine extends AbstractFormEngine {

    public StartStepFormEngine(Node configurationNode, FormParagraph configurationParagraph,RenderingContext context) {
        super(configurationNode, configurationParagraph, context);
    }

    /**
     * Finds the token from a requests parameter, or since this is the first step creates a new form state if the form
     * is being submitted.
     */
    @Override
    protected String getFormStateToken() throws FormStateTokenMissingException, RepositoryException {
        final String formId = getConfigurationNode().getIdentifier();
        final String formStateToken;

        try {
            formStateToken = super.getFormStateToken();
        } catch (FormStateTokenMissingException e) {
            // The token is allowed to be missing when the first step is submitted.
            if (isFormSubmission()) {
                return createAndSetFormState(formId).getToken();
            }
            throw e;
        }

        // check if resolved form state token is for current form
        if (!StringUtils.equals(formStateToken, formId)) {
            if (isFormSubmission()) {
                // Destroy form state for another form and create new one for current form
                destroyFormState(formStateToken);
                return createAndSetFormState(formId).getToken();
            } else {
                throw new FormStateTokenMissingException();
            }
        }

        return formStateToken;
    }

    private void destroyFormState(String formStateToken) {
        try {
            FormState formState = getFormState(formStateToken);
            FormStateUtil.destroyFormState(formState);
        } catch (NoSuchFormStateException e) {
            // no form state registered for the token -> ignoring
        }
    }

    /**
     * Returns the UUID of the first child page with a paragraph of type {@link FormStepParagraph}.
     */
    @Override
    protected String getNextPage() throws RepositoryException {
        // Find first child with step paragraph
        Node currentPage = context.getMainContent();
        Iterator<Node> conditionParagraphIterator = NavigationUtils.getPageParagraphsOfType(currentPage, "form:components/formCondition").iterator();
        String nextPageUUID = NavigationUtils.findNextPageBasedOnCondition(conditionParagraphIterator, this.getFormState().getValues());
        if(nextPageUUID == null) {
            Iterator<Node> contentIterator = NodeUtil.getNodes(currentPage).iterator();
            nextPageUUID = NavigationUtils.findFirstPageWithParagraphOfType(contentIterator, FormStepParagraph.class);
        }
        return nextPageUUID;
    }
}
