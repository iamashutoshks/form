/**
 * This file Copyright (c) 2010-2011 Magnolia International
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
package info.magnolia.module.form.paragraphs.models.multistep;

import info.magnolia.cms.core.MetaData;
import info.magnolia.cms.core.MgnlNodeType;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.jcr.predicate.AbstractPredicate;
import info.magnolia.jcr.util.MetaDataUtil;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.objectfactory.Components;
import info.magnolia.registry.RegistrationException;
import info.magnolia.rendering.template.TemplateDefinition;
import info.magnolia.rendering.template.registry.TemplateDefinitionRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for finding pages with certain paragraphs.
 */
public class NavigationUtils {

//    @Inject
//    private static TemplateDefinitionRegistry templateDefinitionRegistry;

    private static Logger log = LoggerFactory.getLogger(NavigationUtils.class);

    public static String findFirstPageWithParagraphOfType(Iterator<Node> contentIterator, Class<?> paragraphType) throws RepositoryException {
        while (contentIterator.hasNext()) {
            Node childPage = contentIterator.next();
            if (findParagraphOfType(childPage, paragraphType) != null)
                return NodeUtil.getNodeIdentifierIfPossible(childPage);
        }
        return null;
    }

    public static void advanceIteratorTilAfter(Iterator<Node> iterator, Node content) {
        while (iterator.hasNext()) {
            Node content1 = iterator.next();
            if ( NodeUtil.getNodeIdentifierIfPossible(content1).equals(NodeUtil.getNodeIdentifierIfPossible(content)))
                return;
        }
    }

    public static Node findParagraphOfType(Node content, Class<?> paragraphType) throws RepositoryException {
            Iterable<Node> children = NodeUtil.getNodes(content, MgnlNodeType.NT_COMPONENT);
            for (Node child : children) {
                if (isParagraphOfType(child, paragraphType)) {
                    return child;
                }
            Node x = findParagraphOfType(child, paragraphType);
            if (x != null)
                return x;
            }
        return null;
    }

    public static boolean isParagraphOfType(Node child, Class<?> paragraphType) {
        MetaData metaData = MetaDataUtil.getMetaData(child);
        if (metaData == null) return false;
        String template = metaData.getTemplate();
        if (template == null) return false;
        TemplateDefinition definition;
        try {
            definition = Components.getComponent(TemplateDefinitionRegistry.class).getTemplateDefinition(template);
        } catch (RegistrationException e) {
             throw new RuntimeException(e.getMessage(), e);
        }
        if (definition == null) return false;
        return paragraphType.isAssignableFrom(definition.getClass());
    }

    public static Iterable<Node> getPageParagraphsOfType(Node page, final String componentId) {
        Iterable<Node> paragraphList = new ArrayList<Node>();
            try {
                if(page.hasNode("main")){
                    Node mainAreaContent = page.getNode("main");
                    paragraphList = NodeUtil.getNodes(mainAreaContent, new AbstractPredicate<Node>() {
                        @Override
                        public boolean evaluateTyped(Node content) {
                            MetaData metaData = MetaDataUtil.getMetaData(content);
                            if (metaData == null) return false;
                            String template = metaData.getTemplate();
                            if (template == null) return false;
                            return componentId.equals(template);
                        }
                    });
                }
            } catch (PathNotFoundException e) {
                log.error(e.getMessage(),e);
            } catch (RepositoryException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        return paragraphList;
    }

    /**
     * @deprecated use findNextPageBasedOnCondition instead.
     */
    public static String findNextPageBasedOnCriteria(Iterator<Node> criteriaParagraphIterator, Map<String, Object> parameters) {
        while (criteriaParagraphIterator.hasNext()) {
            Node criteriaParagraphContent = criteriaParagraphIterator.next();
            String linkUUID = PropertyUtil.getString(criteriaParagraphContent, "link", "");
            try {
                if(criteriaParagraphContent.hasNode("criteria")){
                    Node criteriaNode = criteriaParagraphContent.getNode("criteria");
                    Iterable<Node> criteriaCollection = NodeUtil.getNodes(criteriaNode);
                    boolean passed = true;
                    for (Iterator<Node> iterator = criteriaCollection.iterator(); iterator.hasNext();) {
                        Node content = iterator.next();
                        passed = evaluateCondition(content, parameters, passed);
                    }
                    if(passed) {
                        return linkUUID;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * @deprecated use evaluateCondition instead.
     */
    public static boolean evaluateCriteria(Node criteriaNode, Map<String, Object> parameters, boolean passed) {

        String condition = PropertyUtil.getString(criteriaNode, "condition");
        String fieldName = PropertyUtil.getString(criteriaNode, "fieldName");
        String fieldValue = PropertyUtil.getString(criteriaNode, "fieldValue");
        String value = "";
        if(parameters.containsKey(fieldName)) {
            value = (String) parameters.get(fieldName);
        }
        return evaluateCondition(fieldValue, value, condition, passed);
    }

    public static String findNextPageBasedOnCondition(Iterator<Node> conditionParagraphIterator, Map<String, Object> parameters) {
        while (conditionParagraphIterator.hasNext()) {
            Node conditionParagraphContent = conditionParagraphIterator.next();
            String linkUUID = PropertyUtil.getString(conditionParagraphContent, "link", "");
            try {
                if(conditionParagraphContent.hasNode("condition")){
                    Node conditionNode = conditionParagraphContent.getNode("condition");
                    Iterable<Node> conditionCollection =  NodeUtil.getNodes(conditionNode);
                    boolean passed = true;
                    for (Iterator<Node> iterator = conditionCollection.iterator(); iterator.hasNext();) {
                        Node content = iterator.next();
                        passed = evaluateCondition(content, parameters, passed);
                    }
                    if(passed) {
                        return linkUUID;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return null;
    }

    public static boolean evaluateCondition(Node conditionNode, Map<String, Object> parameters, boolean passed) {

        String condition = PropertyUtil.getString(conditionNode, "condition");
        String fieldName = PropertyUtil.getString(conditionNode, "fieldName");
        String fieldValue = PropertyUtil.getString(conditionNode, "fieldValue");
        String value = "";
        if(parameters.containsKey(fieldName)) {
            value = (String) parameters.get(fieldName);
        }
        return evaluateCondition(fieldValue, value, condition, passed);
    }

    public static boolean evaluateCondition(String fieldValue, String value, String condition, boolean passed) {
        if (condition.equals("and")) {
            passed &= fieldValue.equals(value);
        } else if (condition.equals("or")) {
            passed |= fieldValue.equals(value);
        } else if (condition.equals("not")) {
            passed &= !fieldValue.equals(value);
        }
        return passed;

    }

    public static Node findParagraphParentPage(Node paragraph) throws AccessDeniedException, PathNotFoundException, RepositoryException {

        Node page = paragraph;

        while(page != null && !NodeUtil.isNodeType(page, MgnlNodeType.NT_PAGE)) {
            page = page.getParent();
        }

        return page;
    }
}
