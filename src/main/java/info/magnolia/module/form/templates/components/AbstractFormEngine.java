/**
 * This file Copyright (c) 2010-2015 Magnolia International
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

import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.wrapper.HTMLEscapingNodeWrapper;
import info.magnolia.jcr.wrapper.I18nNodeWrapper;
import info.magnolia.module.form.engine.FormDataBinder;
import info.magnolia.module.form.engine.FormEngine;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.engine.RedirectView;
import info.magnolia.module.form.engine.View;
import info.magnolia.module.form.processors.FormProcessor;
import info.magnolia.module.form.processors.FormProcessorFailedException;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.context.RenderingContext;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 * Implements common functionality used by both the first step and subsequent steps.
 */
public abstract class AbstractFormEngine extends FormEngine {

    private Node configurationNode;
    private final FormParagraph configurationParagraph;

    protected AbstractFormEngine(Node configurationNode, FormParagraph configurationParagraph, RenderingContext context) {
        super(context);
        this.configurationNode = NodeUtil.deepUnwrap(configurationNode, HTMLEscapingNodeWrapper.class);
        if (!NodeUtil.isWrappedWith(this.configurationNode, I18nNodeWrapper.class)) {
            this.configurationNode = new I18nNodeWrapper(this.configurationNode);
        }
        this.configurationParagraph = configurationParagraph;
        this.redirectWithParams = this.configurationParagraph.isRedirectWithParams();
    }

    public Node getConfigurationNode() {
        return configurationNode;
    }

    public FormParagraph getConfigurationParagraph() {
        return configurationParagraph;
    }

    @Override
    protected View handleNoSuchFormState(String formStateToken) throws RepositoryException {
        return new SessionExpiredView(NodeUtil.getNodeIdentifierIfPossible(context.getMainContent()));
    }

    @Override
    protected View getProcessorFailedView(String errorMessage) {
        // If no message or key is supplied, use default error message
        if (errorMessage == null) {
            errorMessage = getErrorMessage("form.user.errorMessage.generic");
        } else {
            errorMessage = getErrorMessage(errorMessage);
            // If the message is blank in the resource bundle, use default error message
            if (StringUtils.isBlank(errorMessage)) {
                errorMessage = getErrorMessage("form.user.errorMessage.generic");
            }
        }
        return new ErrorView(errorMessage);
    }

    private String getErrorMessage(String errorMessage) {
        Messages messages = MessagesManager.getMessages(getConfigurationParagraph().getI18nBasename());
        Messages defaultMessages = MessagesManager.getMessages(DefaultFormDataBinder.getDefaultPath());
        errorMessage = messages.getWithDefault(errorMessage, defaultMessages.getWithDefault(errorMessage, errorMessage));
        return errorMessage;
    }

    @Override
    protected View getSuccessView() throws RepositoryException {

        // Redirect to success page if there is one
        String successPage = PropertyUtil.getString(configurationNode, "redirect");
        if (StringUtils.isNotEmpty(successPage)) {
            return new RedirectView(successPage);
        }

        SuccessView successView = new SuccessView();
        successView.setSuccessTitle(PropertyUtil.getString(configurationNode, "successTitle"));
        successView.setSuccessMessage(PropertyUtil.getString(configurationNode, "successMessage"));
        return successView;
    }

    @Override
    protected View getFormView(FormStepState step) throws RepositoryException {
        FormView formView = new FormView();
        formView.setValidationErrors(step != null ? step.getValidationErrors() : new HashMap<String, String>());
        formView.setErrorTitle(PropertyUtil.getString(configurationNode, "errorTitle"));
        return formView;
    }

    @Override
    protected FormDataBinder getFormDataBinder() {
        FormDataBinder formDataBinder = Components.newInstance(FormDataBinder.class);
        if (formDataBinder instanceof DefaultFormDataBinder) {
            ((DefaultFormDataBinder) formDataBinder).setI18nBasename(getConfigurationParagraph().getI18nBasename());
        }
        return formDataBinder;
    }

    @Override
    protected void executeProcessors(Map<String, Object> parameters) throws RepositoryException, FormProcessorFailedException {
        for (FormProcessor processor : configurationParagraph.getFormProcessors()) {
            processor.process(configurationNode, parameters);
        }
    }
}
