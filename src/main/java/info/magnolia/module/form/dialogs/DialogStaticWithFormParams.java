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

import info.magnolia.cms.gui.dialog.DialogStatic;
import info.magnolia.jcr.predicate.AbstractPredicate;
import info.magnolia.jcr.util.NodeTypes.Renderable;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.form.templates.components.multistep.NavigationUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 * static field that will display all form params that can be used as freemarker
 * params.
 *
 * @author tmiyar
 *
 */
public class DialogStaticWithFormParams extends DialogStatic {

    @Override
    public void drawHtml(Writer out) throws IOException {
        this.drawHtmlPre(out);
        Node storageNode = null;
        if (getStorageNode() != null) {
            storageNode = getStorageNode().getJCRNode();
        }

        String value = "";
        try {
            Node formStartPage = NavigationUtils
                    .findParagraphParentPage(storageNode);
            Iterable<Node> formControls = findAllFormControlNames(formStartPage);

            for (Node control : formControls) {
                value += PropertyUtil.getString(control, "controlName") + ", ";
            }

            if (StringUtils.isNotEmpty(value)) {
                value = StringUtils.removeEnd(value, ", ");
                value += ".";
            }
        } catch (Exception e) {
            // do nothing
        }
        out.write(value);
        this.drawHtmlPost(out);

    }

    protected Iterable<Node> findAllFormControlNames(Node contentParagraph)
            throws RepositoryException {
        Iterable<Node> nodes = NodeUtil.collectAllChildren(contentParagraph,
                new AbstractPredicate<Node>() {
                    @Override
                    public boolean evaluateTyped(Node content) {
                        try {
                            final String template = Renderable.getTemplate(content);
                            return content.hasProperty("controlName")
                                    && !"form:components/formGroupEdit".equals(template)
                                    && !"form:components/formGroupFields".equals(template)
                                    && !"form:components/formSubmit".equals(template)
                                    && !"form:components/formHoneypot".equals(template)
                            ;
                        } catch (RepositoryException e) {
                            return false;
                        }
                    }
                });

        // order must be same as in the form
        Collections.sort(NodeUtil.asList(nodes), new Comparator<Node>() {
            @Override
            public int compare(final Node content1, final Node content2) {
                try {
                    return ("" + content2.getDepth()).compareTo(""
                            + content1.getDepth());
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
