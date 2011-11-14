/**
 * This file Copyright (c) 2008-2011 Magnolia International
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

import java.util.Map;

import javax.jcr.Node;

/**
 * Processes a submitted form.
 *
 * @author tmiyar
 */
public interface FormProcessor {

    /**
     * Performs an action when a form is submitted and validates successfully. When encountering errors the
     * FormProcessor can throw FormProcessorFailedException with a messages suitable to be displayed to the user. If it
     * throws a runtime exception it is logged and a generic error message is displayed to the user.
     *
     * @param content    the node used to configure the processor, this is where it can find its settings
     * @param parameters a map of the parameters collected from the form
     * @throws FormProcessorFailedException when an error occurs and the FormProcessor has a message that is suitable error message for the user
     */
    void process(Node content, Map<String, Object> parameters) throws FormProcessorFailedException;
}
