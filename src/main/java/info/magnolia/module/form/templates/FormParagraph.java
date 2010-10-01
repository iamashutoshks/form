/**
 * This file Copyright (c) 2008-2010 Magnolia International
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
package info.magnolia.module.form.templates;

import info.magnolia.module.form.processing.FormProcessor;
import info.magnolia.module.templating.Paragraph;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Paragraph customization for the form paragraph, enables configuration of FormProcessors and sub paragraphs.
 *
 * @author tmiyar
 * @see info.magnolia.module.form.processing.FormProcessor
 */
public class FormParagraph extends Paragraph {

    private FormProcessor[] formProcessors = new FormProcessor[0];

    // List<ParagraphConfig>
    private List paragraphs = new ArrayList();

    public FormProcessor[] getFormProcessors() {
        return formProcessors;
    }

    public void addFormProcessor(FormProcessor formProcessor) {
        formProcessors = (FormProcessor[]) ArrayUtils.add(formProcessors, formProcessor);
    }

    public void setFormProcessors(FormProcessor[] formProcessors) {
        this.formProcessors = formProcessors;
    }

    // List<ParagraphConfig>

    public List getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List paragraphs) {
        this.paragraphs = paragraphs;
    }

    public void addParagraph(ParagraphConfig paragraph) {
        this.paragraphs.add(paragraph);
    }
}
