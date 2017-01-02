/**
 * This file Copyright (c) 2013-2017 Magnolia International
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
package info.magnolia.module.form.fields.factory;

import info.magnolia.jcr.predicate.AbstractPredicate;
import info.magnolia.jcr.util.NodeTypes.Renderable;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.form.fields.definition.StaticFormFieldDefinition;
import info.magnolia.module.form.templates.components.multistep.NavigationUtils;
import info.magnolia.ui.form.field.factory.StaticFieldFactory;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

/**
 * Field Factory that will display all form params that can be used as freemarker params.
 */
public class StaticFormFieldFactory extends StaticFieldFactory<StaticFormFieldDefinition> {
    private static final Logger log = LoggerFactory.getLogger(StaticFormFieldFactory.class);

    public StaticFormFieldFactory(StaticFormFieldDefinition definition, Item relatedFieldItem) {
        super(definition, relatedFieldItem);
    }

    @Override
    public String createFieldValue() {
        String value = "";
        try {
            Node storageNode = ((JcrNodeAdapter) item).getJcrItem();
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
        } catch (RepositoryException re) {
            log.warn("Not able to retrieve the xxx for ItemId: " + ((JcrNodeAdapter) item).getItemId());
        }
        return value;
    }

    protected Iterable<Node> findAllFormControlNames(Node contentParagraph)
            throws RepositoryException {
        Iterable<Node> nodes = NodeUtil.collectAllChildren(contentParagraph,
                new AbstractPredicate<Node>() {
                    @Override
                    public boolean evaluateTyped(Node node) {
                        try {
                            final String template = Renderable.getTemplate(node);
                            return node.hasProperty("controlName")
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
        List<Node> list = NodeUtil.asList(nodes);
        Collections.sort(list, new Comparator<Node>() {
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
        return list;
    }
}
