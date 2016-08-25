/**
 * This file Copyright (c) 2008-2016 Magnolia International
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
package info.magnolia.module.form;

import info.magnolia.module.form.validators.Validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;

/**
 * Module class for the form module. Maintains a registry of validators.
 */
public class FormModule {

    private List validators = new ArrayList();

    private static FormModule instance;

    public FormModule() {
        instance = this;
    }

    /**
     * @deprecated since 2.3.1, use IoC instead
     */
    public static FormModule getInstance() {
        return instance;
    }

    public List getValidators() {
        return validators;
    }

    public Validator getValidatorByName(final String name) {

        return (Validator) CollectionUtils.find(this.validators, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return StringUtils.equals(((Validator) object).getName(), name);
            }
        });
    }

    public void setValidators(List validators) {
        this.validators = validators;
    }

    public void addValidators(Validator validator) {
        this.validators.add(validator);
    }
}
