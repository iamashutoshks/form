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
package info.magnolia.module.form.engine;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Binding and validation state for a step.
 */
public class FormStepState implements Serializable {

    private String paragraphUuid;
    private Map<String, FormField> fields = new LinkedHashMap<String, FormField>();

    public String getParagraphUuid() {
        return paragraphUuid;
    }

    public void setParagraphUuid(String paragraphUuid) {
        this.paragraphUuid = paragraphUuid;
    }

    public void add(FormField field) {
        fields.put(field.getName(), field);
    }

    public Map<String, FormField> getFields() {
        return fields;
    }

    public FormField get(String name) {
        return fields.get(name);
    }

    public boolean isValid() {
        for (FormField field : fields.values()) {
            if (!field.isValid())
                return false;
        }
        return true;
    }

    public Map<String, Object> getValues() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (FormField field : fields.values()) {
            map.put(field.getName(), field.getValue());
        }
        return map;
    }

    public Map<String, String> getValidationErrors() {
        Map<String, String> validationErrors = new LinkedHashMap<String, String>();
        for (FormField field : fields.values()) {
            if (!field.isValid())
                validationErrors.put(field.getName(), field.getErrorMessage());
        }
        return validationErrors;
    }
}
