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
package info.magnolia.module.form.processing;

import info.magnolia.module.form.paragraphs.models.FormModel;
import junit.framework.TestCase;

public class DefaultProcessingTest extends TestCase {

    private static class MockFormProcessor implements FormProcessor {

        private boolean enabled = true;
        private boolean processed = false;
        private String returnValue = "";

        public String process(FormModel model) {
            processed = true;
            return returnValue;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isProcessed() {
            return processed;
        }

        public void setReturnValue(String returnValue) {
            this.returnValue = returnValue;
        }
    }

    public void testProcessesAllProcessors() {

        MockFormProcessor processor1 = new MockFormProcessor();
        MockFormProcessor processor2 = new MockFormProcessor();

        DefaultProcessing processing = new DefaultProcessing();
        String returnValue = processing.process(new FormProcessor[]{processor1, processor2}, null);

        assertEquals("", returnValue);
        assertTrue(processor1.isProcessed());
        assertTrue(processor2.isProcessed());
    }

    public void testSkipsDisabledProcessors() {

        MockFormProcessor processor1 = new MockFormProcessor();
        MockFormProcessor processor2 = new MockFormProcessor();

        processor1.setEnabled(false);

        DefaultProcessing processing = new DefaultProcessing();
        String returnValue = processing.process(new FormProcessor[]{processor1, processor2}, null);

        assertEquals("", returnValue);
        assertFalse(processor1.isProcessed());
        assertTrue(processor2.isProcessed());
    }

    public void testBreaksOnFirstError() {

        MockFormProcessor processor1 = new MockFormProcessor();
        MockFormProcessor processor2 = new MockFormProcessor();

        processor1.setReturnValue("error occurred");

        DefaultProcessing processing = new DefaultProcessing();
        String returnValue = processing.process(new FormProcessor[]{processor1, processor2}, null);

        assertEquals("error occurred", returnValue);
        assertTrue(processor1.isProcessed());
        assertFalse(processor2.isProcessed());
    }
}
