/**
 * This file Copyright (c) 2015 Magnolia International
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
package info.magnolia.module.form.validators;

/**
 * This class extends {@link Validator} and provides a validate method which knows both the name and the value of the field to validate.
 * Inherit from this class when your validation class requires both the field name and its value.<br/>
 * If you only need the value of the field inherit directly from {@link Validator} instead.<br/>
 * Note that the default implementation of {@link info.magnolia.module.form.engine.FormDataBinder} ({@link info.magnolia.module.form.templates.components.DefaultFormDataBinder})
 * either checks<br/>
 * - validateWithResult(String value, String fieldName) or<br/>
 * - validateWithResult(String value)<br/>
 * but not both.
 */
public abstract class ExtendedValidator extends Validator {

    /**
     * This method validates the input with the given field name and value returning a {@link ValidationResult}.
     */
    public abstract ValidationResult validateWithResult(String value, String fieldName);

    @Override
    public final ValidationResult validateWithResult(String value) {
        throw new UnsupportedOperationException("This class doesn't support validateWithResult(value).");
    }
}
