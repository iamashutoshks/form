/**
 * This file Copyright (c) 2008-2012 Magnolia International
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
package info.magnolia.module.form.dialogs;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.Content.ContentFilter;
import info.magnolia.cms.gui.dialog.DialogStatic;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.form.paragraphs.models.multistep.NavigationUtils;

/**
 * static field that will display all form params that can be used as
 * freemarker params.
 * @author tmiyar
 *
 */
public class DialogStaticWithFormParams extends DialogStatic {

    @Override
    public void drawHtml(Writer out) throws IOException {
        this.drawHtmlPre(out);
        Content storageNode = getStorageNode();
        String value = "";
        try {
            Content formStartPage = NavigationUtils.findParagraphParentPage(storageNode);
            Collection<Content> formControls = findAllFormControlNames(formStartPage);

            for (Content control : formControls) {
                value += NodeDataUtil.getString(control, "controlName") + ", ";
            }

            if (StringUtils.isNotEmpty(value)) {
                value = StringUtils.removeEnd(value, ", ");
                value += ".";
            }
        } catch (Exception e) {
            //do nothing
        }
        out.write(StringEscapeUtils.escapeHtml(value)); //escape to prevent XSS attack into dialog
        this.drawHtmlPost(out);

    }

    protected Collection<Content> findAllFormControlNames(Content contentParagraph) {
        List<Content> nodes = ContentUtil.collectAllChildren(contentParagraph, new ContentFilter() {

            public boolean accept(Content content) {
                try {
                    return content.hasNodeData("controlName")
                        && !content.getTemplate().equals("formGroupEdit")
                        && !content.getTemplate().equals("formGroupFields")
                        && !content.getTemplate().equals("formSubmit");
                } catch (RepositoryException e) {
                    return false;
                }
            }

        });

        //order must be same as in the form
        Collections.sort(nodes, new Comparator() {

            public int compare(final Object arg0, final Object arg1) {
                Content content1 = (Content) arg0;
                Content content2 = (Content) arg1;

                try {
                    return ("" + content2.getLevel()).compareTo("" + content1.getLevel());
                } catch (PathNotFoundException e) {
                    return 0;
                } catch (RepositoryException e) {
                    return 0;
                }
            }

        });
        return nodes;


    }

}
