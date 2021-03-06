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

import info.magnolia.cms.beans.runtime.MultipartForm;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.i18n.MessagesUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes.Renderable;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.engine.FormDataBinder;
import info.magnolia.module.form.engine.FormField;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.validators.ExtendedValidator;
import info.magnolia.module.form.validators.ValidationResult;
import info.magnolia.module.form.validators.Validator;
import info.magnolia.objectfactory.Components;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;
import info.magnolia.util.EscapeUtil;

import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link info.magnolia.module.form.engine.FormDataBinder} that performs binding and validation for the
 * built-in fields.
 */
public class DefaultFormDataBinder implements FormDataBinder {

    private static final Logger log = LoggerFactory.getLogger(DefaultFormDataBinder.class);

    private static final String CONTENT_NAME_TEXT_FIELD_GROUP = "edits";

    private static final String DEFAULT_PATH = "info.magnolia.module.form.messages";

    private String i18nBasename;

    private final TemplateDefinitionRegistry templateDefinitionRegistry;
    private final FormModule formModule;

    /**
     * @deprecated since 2.3. Use {@link #DefaultFormDataBinder(info.magnolia.rendering.template.registry.TemplateDefinitionRegistry, info.magnolia.module.form.FormModule)} instead.
     */
    @Deprecated
    public DefaultFormDataBinder() {
        this(Components.getComponent(TemplateDefinitionRegistry.class), Components.getComponent(FormModule.class));
    }

    @Inject
    public DefaultFormDataBinder(TemplateDefinitionRegistry templateDefinitionRegistry, FormModule formModule) {
        this.templateDefinitionRegistry = templateDefinitionRegistry;
        this.formModule = formModule;
    }

    public void setI18nBasename(String i18nBasename) {
        this.i18nBasename = i18nBasename;
    }

    public static String getDefaultPath() {
        return DEFAULT_PATH;
    }

    @Override
    public FormStepState bindAndValidate(Node component) throws RepositoryException {
        FormStepState step = new FormStepState();
        step.setParagraphUuid(NodeUtil.getNodeIdentifierIfPossible(component));
        if (component.hasNode("fieldsets")) {
            Iterator<Node> itFieldsets = NodeUtil.getNodes(component.getNode("fieldsets")).iterator();
            bindAndValidateFieldset(itFieldsets, step);
        }
        return step;
    }

    private void bindAndValidateFieldset(Iterator<Node> itFieldsets, FormStepState step) throws RepositoryException {
        while (itFieldsets.hasNext()) {
            Node fieldset = itFieldsets.next();
            if (fieldset.hasNode("fields")) {
                Iterator<Node> iterator = NodeUtil.getNodes(fieldset.getNode("fields")).iterator();
                bindAndValidateFields(iterator, step);
            }
        }
    }

    /**
     * Besides validating the fields for this step, it will also check for the existence of a <code>escapeHtml</code> property in a field configuration.
     * If such property value is false, HTML won't be escaped. Default value is true.
     */
    protected void bindAndValidateFields(Iterator<Node> iterator, FormStepState step) throws RepositoryException {
        while (iterator.hasNext()) {
            final Node node = iterator.next();

            if (node.hasProperty("controlName")) {
                final String controlName = node.getProperty("controlName").getString();
                String values = StringUtils.join(MgnlContext.getParameterValues(controlName), "__");

                // In case multiple empty values we should keep them as empty
                if (values != null) {
                    values = Pattern.matches("^__+$", values) ? StringUtils.EMPTY : values;
                }

                final boolean escapeHtml = this.shouldEscapeHtml(node);
                final String value = escapeHtml ? EscapeUtil.escapeXss(values) : values;

                FormField field = new FormField();
                field.setName(controlName);
                field.setValue(value);
                step.add(field);

                if (StringUtils.isEmpty(value) && isMandatory(node)) {
                    field.setErrorMessage(getErrorMessage("mandatory", node));
                } else if ((StringUtils.isNotEmpty(value) || isFileFieldWithUploadedFile(node, controlName))
                        && node.hasProperty("validation")) { // Info.nl change
                    for (String validatorName : getValidatorNames(node)) {
                        Validator validator = formModule.getValidatorByName(validatorName);
                        if (validator != null) {
                            ValidationResult validationResult;
                            // if validator is an 'extended validator' pass on control name
                            if (validator instanceof ExtendedValidator) {
                                validationResult = ((ExtendedValidator) validator).validateWithResult(value, controlName);
                            } else {
                                validationResult = validator.validateWithResult(value);
                            }
                            if (!validationResult.isSuccess()) {
                                field.setErrorMessage(getValidatorErrorMessage(validator, validationResult, node));
                            }
                        }
                    }
                } else if (node.hasNode(CONTENT_NAME_TEXT_FIELD_GROUP)) {
                    Iterator<Node> textFieldGroup = NodeUtil.getNodes(node.getNode(CONTENT_NAME_TEXT_FIELD_GROUP)).iterator();
                    bindAndValidateFields(textFieldGroup, step);
                }
            }
        }
    }

    /**
     * Checks if node is of type attachment (i.e. file input field) and if so, checks if related file has been
     * uploaded as part of posted form data.
     *
     * @return true if node if of type 'attachment' and file with provided control name has been posted in form; false otherwise
     */
    private boolean isFileFieldWithUploadedFile(Node node, String controlName) throws RepositoryException {
        boolean isFileFieldWithUploadedFile = false;
        if (node.hasProperty("type") && "attachment".equals(node.getProperty("type").getString())) {
            log.debug("controlName: {} is of type attachment", controlName);
            // now check if file has actually been uploaded
            MultipartForm form = MgnlContext.getWebContext().getPostedForm();
            if (form.getDocuments().containsKey(controlName)) {
                isFileFieldWithUploadedFile = true;
            }
        }
        return isFileFieldWithUploadedFile;
    }

    /**
     * Gets validator names from <code>validation</code> property.
     */
    private String[] getValidatorNames(Node node) throws RepositoryException {
        Property validationProperty = node.getProperty("validation");

        Value[] values;
        if (validationProperty.isMultiple()) {
            values = validationProperty.getValues();
        } else {
            values = new Value[]{validationProperty.getValue()};
        }

        return PropertyUtil.getValuesStringList(values).toArray(new String[values.length]);
    }

    protected boolean isMandatory(Node node) {
        return PropertyUtil.getBoolean(node, "mandatory", false);
    }

    private String getValidatorErrorMessage(Validator validator, ValidationResult validationResult, Node node) {

        // If the validator returned an error message will use it, possible with a resource bundle configured on the validator itself
        if (StringUtils.isNotEmpty(validationResult.getErrorMessage())) {
            return getErrorMessage(validationResult.getErrorMessage(), "invalid input", node, validator.getI18nBasename());
        }

        // Otherwise we'll default to a key of format: form.user.errorMessage.<validator name>
        return getErrorMessage(validator.getName(), node);
    }

    protected String getErrorMessage(String message, Node node) {
        return getErrorMessage("form.user.errorMessage." + message, "invalid input", node, null);
    }

    private String getErrorMessage(String key, String defaultMsg, Node node, String overridingResourceBundle) {

        Locale locale = I18nContentSupportFactory.getI18nSupport().getLocale();

        Messages messages = MessagesManager.getMessages(getDefaultPath());

        messages = MessagesUtil.chain(MessagesManager.getMessages(i18nBasename, locale), messages);

        if (overridingResourceBundle != null) {
            messages = MessagesUtil.chain(MessagesManager.getMessages(overridingResourceBundle, locale), messages);
        }

        String errorMessage = messages.getWithDefault(key, defaultMsg);
        String title = PropertyUtil.getString(node, "title");
        return title + ": " + errorMessage;
    }

    private boolean shouldEscapeHtml(Node node) {
        try {
            String template = Renderable.getTemplate(node);
            if (template != null) {
                final TemplateDefinition templateDefinition = templateDefinitionRegistry.getProvider(template).get();
                if (templateDefinition instanceof FormFieldTemplate) {
                    return ((FormFieldTemplate) templateDefinition).isEscapeHtml();
                }
            } else {
                log.warn("Could not find mgnl:template for node at {}", node.getPath());
            }
        } catch (RepositoryException e) {
            log.warn("Could not find mgnl:template for {}", node);
        }
        return true; //keep compatibility, true by default
    }
}
