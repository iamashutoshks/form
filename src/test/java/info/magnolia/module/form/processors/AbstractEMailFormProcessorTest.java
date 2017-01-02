/**
 * This file Copyright (c) 2016-2017 Magnolia International
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
package info.magnolia.module.form.processors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;

import org.junit.Before;
import org.junit.Test;

public class AbstractEMailFormProcessorTest {

    private AbstractEMailFormProcessor emailFormProcessor;
    private Map<String, Object> parameters;

    @Before
    public void setUp() {
        emailFormProcessor = new AbstractEMailFormProcessor() {
            @Override
            protected void internalProcess(Node content, Map<String, Object> parameters) throws FormProcessorFailedException {
            }
        };

        parameters = new HashMap<>();
        parameters.put("email", "vincent.gombert@solucom.fr");
        parameters.put("subject", "Vincent&#039;s apostrophe test");
        parameters.put("message", "Can&#039;t use neither single quotes nor &quot;double quotes&quot;");
    }

    @Test
    public void resolveParametersWithContentTypeIsNull() {
        // WHEN
        Map<String, Object> actualParameters = emailFormProcessor.resolveParameters(null, parameters);

        // THEN
        assertThat(actualParameters, allOf(
                        hasEntry("email", (Object) "vincent.gombert@solucom.fr"),
                        hasEntry("subject", (Object) "Vincent&#039;s apostrophe test"),
                        hasEntry("message", (Object) "Can&#039;t use neither single quotes nor &quot;double quotes&quot;"))
        );
    }

    @Test
    public void resolveParametersWithContentTypeIsHtml() {
        // WHEN
        Map<String, Object> actualParameters = emailFormProcessor.resolveParameters("html", parameters);

        // THEN
        assertThat(actualParameters, allOf(
                        hasEntry("email", (Object) "vincent.gombert@solucom.fr"),
                        hasEntry("subject", (Object) "Vincent&#039;s apostrophe test"),
                        hasEntry("message", (Object) "Can&#039;t use neither single quotes nor &quot;double quotes&quot;"))
        );
    }

    @Test
    public void resolveParametersWithContentTypeIsText() {
        // WHEN
        Map<String, Object> actualParameters = emailFormProcessor.resolveParameters("text", parameters);

        // THEN
        assertThat(actualParameters, allOf(
                        hasEntry("email", (Object) "vincent.gombert@solucom.fr"),
                        hasEntry("subject", (Object) "Vincent's apostrophe test"),
                        hasEntry("message", (Object) "Can't use neither single quotes nor \"double quotes\""))
        );
    }
}