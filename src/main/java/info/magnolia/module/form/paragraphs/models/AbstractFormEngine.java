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
package info.magnolia.module.form.paragraphs.models;

import java.util.HashMap;
import java.util.Map;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.engine.FormDataBinder;
import info.magnolia.module.form.engine.FormEngine;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.engine.RedirectView;
import info.magnolia.module.form.engine.View;
import info.magnolia.module.form.processors.FormProcessor;
import info.magnolia.module.form.templates.FormParagraph;

/**
 * Implements common functionality used by both the first step and subsequent steps.
 */
public abstract class AbstractFormEngine extends FormEngine {

    private Content configurationNode;
    private FormParagraph configurationParagraph;

    protected AbstractFormEngine(Content configurationNode, FormParagraph configurationParagraph) {
        this.configurationNode = configurationNode;
        this.configurationParagraph = configurationParagraph;
    }

    public Content getConfigurationNode() {
        return configurationNode;
    }

    public FormParagraph getConfigurationParagraph() {
        return configurationParagraph;
    }

    @Override
    protected View handleNoSuchFormState(String formStateToken) throws RepositoryException {
        return new SessionExpiredView(MgnlContext.getAggregationState().getMainContent().getUUID());
    }

    @Override
    protected View getProcessorFailedView(String result) {
        return new ErrorView(result);
    }

    @Override
    protected View getSuccessView() throws RepositoryException {

        // Redirect to success page if there is one
        String successPage = NodeDataUtil.getString(configurationNode, "redirect");
        if (StringUtils.isNotEmpty(successPage)) {
            return new RedirectView(successPage);
        }

        SuccessView successView = new SuccessView();
        successView.setSuccessTitle(NodeDataUtil.getString(configurationNode, "successTitle"));
        successView.setSuccessMessage(NodeDataUtil.getString(configurationNode, "successMessage"));
        return successView;
    }

    @Override
    protected View getFormView(FormStepState step) throws RepositoryException {
        FormView formView = new FormView();
        formView.setValidationErrors(step != null ? step.getValidationErrors() : new HashMap<String, String>());
        formView.setErrorTitle(NodeDataUtil.getString(configurationNode, "errorTitle"));
        return formView;
    }

    @Override
    protected FormDataBinder getFormDataBinder() {
        return new DefaultFormDataBinder();
    }

    @Override
    protected String executeProcessors(Map<String, String> parameters) throws RepositoryException {

        FormProcessor[] processors = configurationParagraph.getFormProcessors();
        for (FormProcessor processor : processors) {
            String result = processor.process(configurationNode, parameters);
            if (StringUtils.isNotEmpty(result))
                return result;
        }
        return null;
    }
}
