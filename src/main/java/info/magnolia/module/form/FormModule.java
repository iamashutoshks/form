/**
 * This file Copyright (c) 2007-2008 Magnolia International
 * Ltd.  (http://www.magnolia.info). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia.info/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.form;

import info.magnolia.module.form.validations.Validation;

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

    public List getValidators() {
        return validators;
    }

    public void setValidators(List validators) {
        this.validators = validators;
    }

    public void addValidators(Validation validator) {
        this.validators.add(validators);
    }


}
