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
package info.magnolia.module.form.templates.components;

import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.form.engine.FormField;
import info.magnolia.module.form.engine.FormState;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.engine.View;
import info.magnolia.rendering.model.RenderingModel;
import info.magnolia.rendering.model.RenderingModelImpl;
import info.magnolia.rendering.template.RenderableDefinition;
import info.magnolia.templating.functions.TemplatingFunctions;

import java.io.IOException;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements typical behavior in extensions points for classes that use FormEngine. Subclasses provides a FormEngine
 * instance and can override methods called by nested paragraphs to change default behavior.
 *
 * @param <RD> - an instance of {@link RenderableDefinition}
 */
public abstract class AbstractFormModel<RD extends RenderableDefinition> extends RenderingModelImpl<RD> {

    private final static Logger logger = LoggerFactory.getLogger(AbstractFormModel.class);

    private AbstractFormEngine formEngine;
    private View view;
    protected final TemplatingFunctions functions;

    @Inject
    protected AbstractFormModel(Node content, RD definition, RenderingModel<?> parent, TemplatingFunctions functions) {
        super(content, definition, parent);
        this.functions = functions;
    }

    protected abstract AbstractFormEngine createFormEngine() throws RepositoryException;

    @Override
    public String execute() {
        try {

            if (MgnlContext.isWebContext()) {
                MgnlContext.getWebContext().getResponse().setHeader("Cache-Control", "no-cache");
            }

            formEngine = createFormEngine();

            view = formEngine.handleRequest(getNode());

            return view.execute();

        } catch (RepositoryException e) {
            return handleException(e);
        } catch (IOException e) {
            return handleException(e);
        }
    }

    private String handleException(Exception e) {
        logger.error("Exception caught executing form model", e);
        Messages messages = MessagesManager.getMessages(getDefinition().getI18nBasename());
        view = new ErrorView(messages.get("generic"));
        return "failure";
    }

    // These are called from templates and models of inner paragraphs

    public FormState getFormState() {
        return formEngine.getFormState();
    }

    public View getView() {
        return view;
    }

    public String getRequiredSymbol() throws RepositoryException {
        return PropertyUtil.getString(formEngine.getConfigurationNode(), "requiredSymbol", "");
    }

    /**
     * Text for required symbol.
     */
    public String getRightText() throws RepositoryException {
        return PropertyUtil.getString(formEngine.getConfigurationNode(), "rightText", "");
    }

    public FormField getFormField(String name) {
        FormState formState = formEngine.getFormState();
        if (formState == null)
            return null;
        FormStepState step = formState.getStep(NodeUtil.getNodeIdentifierIfPossible(content));
        if (step == null)
            return null;
        return step.get(name);
    }
}
