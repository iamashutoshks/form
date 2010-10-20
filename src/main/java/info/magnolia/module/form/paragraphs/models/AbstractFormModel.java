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

import java.io.IOException;
import java.util.HashMap;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.engine.FormDataBinder;
import info.magnolia.module.form.engine.FormExecutionSkeleton;
import info.magnolia.module.form.engine.FormField;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.engine.RedirectView;
import info.magnolia.module.form.engine.View;
import info.magnolia.module.form.paragraphs.models.multistep.SessionExpiredView;
import info.magnolia.module.form.templates.FormParagraph;
import info.magnolia.module.form.util.FormUtil;
import info.magnolia.module.templating.RenderableDefinition;
import info.magnolia.module.templating.RenderingModel;

/**
 * Implements typical behaviour in extensions points for classes that use FormExecutionSkeleton.
 */
public abstract class AbstractFormModel extends FormExecutionSkeleton {

    private final static Logger logger = LoggerFactory.getLogger(AbstractFormModel.class);

    private View view;

    protected AbstractFormModel(Content content, RenderableDefinition definition, RenderingModel parent) {
        super(content, definition, parent);
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

        Content configurationNode = getConfigurationNode();

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
        formView.setErrorTitle(NodeDataUtil.getString(getConfigurationNode(), "errorTitle"));
        return formView;
    }

    @Override
    protected FormDataBinder getFormDataBinder() {
        return new DefaultFormDataBinder();
    }

    @Override
    public String execute() {
        try {
            view = super.handleRequest();
            return view.execute();
        } catch (RepositoryException e) {
            return handleException(e);
        } catch (IOException e) {
            return handleException(e);
        }
    }

    private String handleException(Exception e) {
        logger.error("Exception caught executing form model", e);
        view = new ErrorView(FormUtil.getMessage("generic"));
        return "failure";
    }

    // These are called from templates and models of inner paragraphs

    public View getView() {
        return view;
    }

    public String getParagraphsAsStringList() throws RepositoryException {
        return StringUtils.join(((FormParagraph) definition).getParagraphs(), ", ");
    }

    public String getRequiredSymbol() throws RepositoryException {
        return NodeDataUtil.getString(getConfigurationNode(), "requiredSymbol", "");
    }

    /**
     * Text for required symbol.
     */
    public String getRightText() throws RepositoryException {
        return NodeDataUtil.getString(getConfigurationNode(), "rightText", "");
    }

    public FormField getFormField(String name) {
        FormStepState step = getFormState().getStep(content.getUUID());
        if (step == null)
            return null;
        return step.get(name);
    }
}
