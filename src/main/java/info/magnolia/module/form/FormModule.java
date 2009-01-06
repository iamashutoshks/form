/**
 * This file Copyright (c) 2008-2009 Magnolia International
 * Ltd.  (http://www.magnolia.info). All rights reserved.
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
 * is available at http://www.magnolia.info/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.form;

import info.magnolia.module.form.validations.Validation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tmiyar
 *
 */
public class FormModule {

    private List requestProcessors = new ArrayList();

    private List validators = new ArrayList();

    private static FormModule instance;

    public FormModule() {
        instance = this;
    }

    public static FormModule getInstance() {
        return instance;
    }


    public List getRequestProcessors() {
        return requestProcessors;
    }

    public void setRequestProcessors(List requestProcessors) {
        this.requestProcessors = requestProcessors;
    }

    public void addRequestProcessors(RequestProcessor requestProcessor) {
        this.requestProcessors.add(requestProcessor);
    }

    public RequestProcessor getRequestProcessor(final String name) {
        return (RequestProcessor) CollectionUtils.find(this.requestProcessors, new Predicate() {
            public boolean evaluate(Object object) {
                return StringUtils.equals(((RequestProcessor) object).getName(), name);
            }
        });

    }

    public List getValidators() {
        return validators;
    }

    public Validation getValidatorByName(final String name) {

        return (Validation) CollectionUtils.find(this.validators, new Predicate() {
            public boolean evaluate(Object object) {
                return StringUtils.equals(((Validation) object).getName(), name);
            }
        });

    }

    public void setValidators(List validators) {
        this.validators = validators;
    }

    public void addValidators(Validation validator) {
        this.validators.add(validator);
    }


}
