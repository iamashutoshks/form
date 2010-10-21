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
package info.magnolia.module.form.engine;

import java.util.Map;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

import info.magnolia.cms.core.Content;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.processors.FormProcessor;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;
import info.magnolia.module.templating.RenderingModelImpl;

/**
 * Base class for RenderingModels that do form processing. Implements a rendering and form submission algorithm that
 * keeps state in session for multiple pages. The behaviour and outcome of the algorithm can be customized by
 * implementing extension hooks.
 */
public abstract class FormExecutionSkeleton extends RenderingModelImpl {

    private FormState formState;

    protected FormExecutionSkeleton(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
    }

    public View handleRequest() throws RepositoryException {

        if (!isFormSubmission()) {

            String formStateToken;
            try {
                formStateToken = FormStateUtil.getFormStateToken();
            } catch (FormStateTokenMissingException e) {
                return handleTokenMissing();
            }

            try {
                formState = FormStateUtil.getFormState(formStateToken);
            } catch (NoSuchFormStateException e) {
                return handleNoSuchFormState(e.getToken());
            }

            View view = formState.getView();
            formState.setView(null);
            if (view == null) {
                return getFormView(formState.getStep(getContent().getUUID()));
            }
            if (formState.isEnded())
                FormStateUtil.destroyFormState(formState);
            return view;

        } else {

            String formStateToken;
            try {
                formStateToken = FormStateUtil.getFormStateToken();
            } catch (FormStateTokenMissingException e) {
                // Cant post without a token... should never happen
                // Redirect the user to this page
                return new RedirectView(MgnlContext.getAggregationState().getMainContent());
            }

            try {
                formState = FormStateUtil.getFormState(formStateToken);
            } catch (NoSuchFormStateException e) {
                return handleNoSuchFormStateOnSubmit(e.getToken());
            }

            View view = processSubmission();

            formState.setView(null);

            if (view instanceof RedirectView) {
                FormStateUtil.destroyFormState(formState);
                return view;
            }

            if (view instanceof RedirectWithTokenView) {
                return view;
            }

            formState.setView(view);
            return new RedirectWithTokenView(MgnlContext.getAggregationState().getMainContent(), formState.getToken());
        }
    }

    /**
     * Performs the processing of submitted values. If this method returns a RedirectView this is treated like an exit
     * and the formState is removed from session.
     */
    private View processSubmission() throws RepositoryException {

        // Validate the input parameters and collect FormField instances
        FormStepState step = getFormDataBinder().bindAndValidate(getContent());

        // Add the submitted fields to formState
        formState.addStep(step);

        // If validation failed proceed and render page
        if (!step.isValid()) {
            return getValidationFailedView(step);
        }

        // Validation succeeded

        View validationSuccessfulView = getValidationSuccessfulView(formState);
        if (validationSuccessfulView != null)
            return validationSuccessfulView;

        // Execute processors
        String result = executeProcessors();

        formState.setEnded(true);

        // If processing failed then render page with an error message
        if (StringUtils.isNotEmpty(result)) {
            return getProcessorFailedView(result);
        }

        // Render page with success message
        return getSuccessView();
    }

    public FormState getFormState() {
        return formState;
    }

    protected boolean isFormSubmission() {
        return MgnlContext.getWebContext().getRequest().getMethod().equals("POST");
    }

    /**
     * Called when the form is to be rendered and there's no token provided. By default it creates a new formState and
     * renders the page.
     */
    protected View handleTokenMissing() throws RepositoryException {

        // Create an empty formState and generate a token
        formState = FormStateUtil.createAndSetFormState();

        return getFormView(null);
    }

    /**
     * Called when the form is to be rendered.
     *
     * @param step is null when we render the page for the first time. I.e. when no validation has taken place.
     */
    protected abstract View getFormView(FormStepState step) throws RepositoryException;

    /**
     * Called when validation has been formed and there were no validation errors. Override this method to add multi
     * step support.
     */
    protected View getValidationSuccessfulView(FormState formState) throws RepositoryException {
        return null;
    }

    /**
     * Called when validation fails.
     */
    protected View getValidationFailedView(FormStepState step) throws RepositoryException {
        return getFormView(step);
    }

    /**
     * Called when a processor failed.
     */
    protected abstract View getProcessorFailedView(String result) throws RepositoryException;

    /**
     * Called when validation was successful and all processors executed successfully.
     */
    protected abstract View getSuccessView() throws RepositoryException;

    /**
     * Called when the form was to be rendered for a supplied form token but there is no state in the session. This
     * typically happens when the user navigates back after having completed the form or if the user returns later
     * via a bookmark or via browser history.
     */
    protected abstract View handleNoSuchFormState(String formStateToken) throws RepositoryException;

    /**
     * Called when a submission occurs with a form state token but there is no formState in session. This typically
     * happens when the user has waited so long to complete the form that the session timed out.
     */
    protected View handleNoSuchFormStateOnSubmit(String formStateToken) throws RepositoryException {
        // Redirect to the current page _with_ the invalid token, this will render an error message.
        return new RedirectWithTokenView(MgnlContext.getAggregationState().getMainContent(), formStateToken);
    }

    protected abstract FormDataBinder getFormDataBinder();

    protected String executeProcessors() throws RepositoryException {

        Map<String, String> parameters = formState.getValues();

        FormProcessor[] processors = getProcessors();
        for (FormProcessor processor : processors) {
            String result = processor.process(getConfigurationNode(), parameters);
            if (StringUtils.isNotEmpty(result))
                return result;
        }
        return null;
    }

    protected abstract FormProcessor[] getProcessors() throws RepositoryException;

    /**
     * Returns the configuration node for the processors. It's passed to them when they are executed so that they
     * can read their settings.
     */
    protected abstract Content getConfigurationNode() throws RepositoryException;
}
