/**
 * This file Copyright (c) 2010-2013 Magnolia International
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
 * State of an ongoing form execution. Stored in session. Maintains state of all submitted steps.
 */
public class FormState implements Serializable {

    private static final long serialVersionUID = 6426588734247055285L;
    private String token;
    //In order to have back button working correctly it is important that steps are stored in a structure keeping their insertion order!
    private final Map<String, FormStepState> steps = new LinkedHashMap<String, FormStepState>();
    private boolean ended;
    private int currentlyExecutingStep = 0;

    /**
     * A view that has been prepared for the next time this form is rendered.
     */
    private View view;

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public Map<String, FormStepState> getSteps() {
        return steps;
    }

    public void addStep(FormStepState stepState) {
        steps.put(stepState.getParagraphUuid(), stepState);
    }

    public FormStepState getStep(String uuid) {
        return steps.get(uuid);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public Map<String, Object> getValues() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for (FormStepState step : steps.values()) {
            map.putAll(step.getValues());
        }
        return map;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
    /**
     * @return the currently executing step in this form. First step is 0. Please notice that it is the responsibility of the {@link FormEngine} implementations
     * to correctly set this count, according to the actions performed on the UI (i.e. hitting the back button should decrease this count by one, unless we're on the first step)
     * and the successful validation of data entered in the form should increase this count by one (unless we're on the last step).
     */
    public int getCurrentlyExecutingStep() {
        return currentlyExecutingStep;
    }

    public void setCurrentlyExecutingStep(int newStep) {
        if(newStep < 0 || newStep > (getSteps().size())) {
            return;
        }
        this.currentlyExecutingStep = newStep;
    }
}
