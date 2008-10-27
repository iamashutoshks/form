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
package info.magnolia.module.form.validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateExpression extends Validation {
    public String expression;

    public boolean validate(String value) {
        Pattern patern = Pattern.compile(this.getExpression());
        Matcher fit = patern.matcher(value);
        if (fit.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
