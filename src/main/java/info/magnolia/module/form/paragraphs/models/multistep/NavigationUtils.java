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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.MetaData;
import info.magnolia.cms.core.Content.ContentFilter;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;
import info.magnolia.module.templating.Paragraph;
import info.magnolia.module.templating.ParagraphManager;

/**
 * Utilities for finding pages with certain paragraphs.
 */
public class NavigationUtils {

    public static String findFirstPageWithParagraphOfType(Iterator<Content> contentIterator, Class<?> paragraphType) {
        while (contentIterator.hasNext()) {
            Content childPage = contentIterator.next();
            if (findParagraphOfType(childPage, paragraphType) != null)
                return childPage.getUUID();
        }
        return null;
    }

    public static void advanceIteratorTilAfter(Iterator<Content> iterator, Content content) {
        while (iterator.hasNext()) {
            Content content1 = iterator.next();
            if (content1.getUUID().equals(content.getUUID()))
                return;
        }
    }

    public static Content findParagraphOfType(Content content, Class<?> paragraphType) {
        Collection<Content> children = content.getChildren(ItemType.CONTENTNODE);
        for (Content child : children) {
            if (isParagraphOfType(child, paragraphType)) {
                return child;
            }
            Content x = findParagraphOfType(child, paragraphType);
            if (x != null)
                return x;
        }
        return null;
    }

    public static boolean isParagraphOfType(Content child, Class<?> paragraphType) {
        MetaData metaData = child.getMetaData();
        if (metaData == null) return false;
        String template = metaData.getTemplate();
        if (template == null) return false;
        Paragraph definition = ParagraphManager.getInstance().getParagraphDefinition(template);
        if (definition == null) return false;
        return paragraphType.isAssignableFrom(definition.getClass());
    }
    
    public static List<Content> getPageParagraphsOfType(Content page, final String paragraphName) {
        Content mainAreaContent = ContentUtil.getContent(page, "main");
        List<Content> paragraphList = new ArrayList<Content>();
        if(mainAreaContent != null) {
            paragraphList = ContentUtil.collectAllChildren(mainAreaContent, new ContentFilter() {
                public boolean accept(Content content) {
                    MetaData metaData = content.getMetaData();
                    if (metaData == null) return false;
                    String template = metaData.getTemplate();
                    if (template == null) return false;
                    return paragraphName.equals(template);
                }
            });
        }
        return paragraphList;
    }
    
    public static String findNextPageBasedOnCriteria(Iterator<Content> criteriaParagraphIterator, Map<String, Object> parameters) {
        while (criteriaParagraphIterator.hasNext()) {
            Content criteriaParagraphContent = criteriaParagraphIterator.next();
            String linkUUID = NodeDataUtil.getString(criteriaParagraphContent, "link", "");
            Content criteriaNode = ContentUtil.getContent(criteriaParagraphContent, "criteria");
            if(criteriaNode != null) {
            
                Collection<Content> criteriaCollection = ContentUtil.getAllChildren(criteriaNode);
                boolean passed = true;
                for (Iterator<Content> iterator = criteriaCollection.iterator(); iterator.hasNext();) {
                    Content content = iterator.next();
                    passed = evaluateCriteria(content, parameters, passed);
                }
                if(passed) {
                    return linkUUID;
                }
            }
        }
        
        return null;
    }
    
    public static boolean evaluateCriteria(Content criteriaNode, Map<String, Object> parameters, boolean passed) {
        
        String condition = NodeDataUtil.getString(criteriaNode, "condition");
        String fieldName = NodeDataUtil.getString(criteriaNode, "fieldName");
        String fieldValue = NodeDataUtil.getString(criteriaNode, "fieldValue");
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
}
