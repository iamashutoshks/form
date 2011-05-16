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
package info.magnolia.module.form.paragraphs.models;

import java.util.Iterator;
import java.util.Locale;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.i18n.I18nContentSupportFactory;
import info.magnolia.cms.i18n.Messages;
import info.magnolia.cms.i18n.MessagesManager;
import info.magnolia.cms.i18n.MessagesUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.form.FormModule;
import info.magnolia.module.form.engine.FormDataBinder;
import info.magnolia.module.form.engine.FormField;
import info.magnolia.module.form.engine.FormStepState;
import info.magnolia.module.form.validators.ValidationResult;
import info.magnolia.module.form.validators.Validator;

/**
 * Default {@link info.magnolia.module.form.engine.FormDataBinder} that performs binding and validation for the
 * built-in fields.
 */
public class DefaultFormDataBinder implements FormDataBinder {

    private static final String CONTENT_NAME_TEXT_FIELD_GROUP = "edits";

    private static final String DEFAULT_PATH = "info.magnolia.module.form.messages";

    private String i18nBasename;

    public void setI18nBasename(String i18nBasename) {
        this.i18nBasename = i18nBasename;
    }

    public static String getDefaultPath(){
        return DEFAULT_PATH;
    }

    public FormStepState bindAndValidate(Content paragraph) throws RepositoryException {
        FormStepState step = new FormStepState();
        step.setParagraphUuid(paragraph.getUUID());
        if (paragraph.hasContent("fieldsets")) {
            Iterator itFieldsets = paragraph.getContent("fieldsets").getChildren().iterator();
            bindAndValidateFieldset(itFieldsets, step);
        }
        return step;
    }

    private void bindAndValidateFieldset(Iterator itFieldsets, FormStepState step) throws RepositoryException {
        while (itFieldsets.hasNext()) {
            Content fieldset = (Content) itFieldsets.next();
            if (fieldset.hasContent("fields")) {
                Iterator iterator = fieldset.getContent("fields").getChildren().iterator();
                bindAndValidateFields(iterator, step);
            }
        }
    }

    protected void bindAndValidateFields(Iterator iterator, FormStepState step) throws RepositoryException {
        while (iterator.hasNext()) {
            final Content node = (Content) iterator.next();

            if (node.hasNodeData("controlName")) {

                final String controlName = node.getNodeData("controlName").getString();
                final String value = StringUtils.join(MgnlContext.getParameterValues(controlName), "__");

                FormField field = new FormField();
                field.setName(controlName);
                field.setValue(value);
                step.add(field);

                if (StringUtils.isEmpty(value) && isMandatory(node)) {
                    field.setErrorMessage(getErrorMessage("mandatory", node));

                } else if (!StringUtils.isEmpty(value) && node.hasNodeData("validation")) {

                    String validatorName = node.getNodeData("validation").getString();
                    Validator validator = FormModule.getInstance().getValidatorByName(validatorName);
                    if (validator != null) {
                        ValidationResult validationResult = validator.validate(value);
                        if (!validationResult.isSuccess()) {
                            field.setErrorMessage(getValidatorErrorMessage(validator, validationResult, node));
                        }
                    }
                } else if (node.hasContent(CONTENT_NAME_TEXT_FIELD_GROUP)) {
                    Iterator textFieldGroup = node.getContent(CONTENT_NAME_TEXT_FIELD_GROUP).getChildren().iterator();
                    bindAndValidateFields(textFieldGroup, step);
                }
            }
        }
    }

    protected boolean isMandatory(Content node) {
        return NodeDataUtil.getBoolean(node, "mandatory", false);
    }

    private String getValidatorErrorMessage(Validator validator, ValidationResult validationResult, Content node) {

        // If the validator returned an error message will use it, possible with a resource bundle configured on the validator itself
        if (StringUtils.isNotEmpty(validationResult.getErrorMessage())) {
            return getErrorMessage(validationResult.getErrorMessage(), "invalid input", node, validator.getI18nBasename());
        }

        // Otherwise we'll default to a key of format: form.user.errorMessage.<validator name>
        return getErrorMessage(validator.getName(), node);
    }

    protected String getErrorMessage(String message, Content node) {
        return getErrorMessage("form.user.errorMessage." + message, "invalid input", node, null);
    }

    private String getErrorMessage(String key, String defaultMsg, Content node, String overridingResourceBundle) {

        Locale locale = I18nContentSupportFactory.getI18nSupport().getLocale();

        Messages messages = MessagesManager.getMessages(getDefaultPath());

        messages = MessagesUtil.chain(MessagesManager.getMessages(i18nBasename, locale), messages);

        if (overridingResourceBundle != null) {
            messages = MessagesUtil.chain(MessagesManager.getMessages(overridingResourceBundle, locale), messages);
        }

        String errorMessage = messages.getWithDefault(key, defaultMsg);
        String title = node.getNodeData("title").getString();
        return title + ": " + errorMessage;
    }
}
