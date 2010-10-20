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
package info.magnolia.module.form.paragraphs.models.multistep;

import java.util.Collection;
import java.util.Iterator;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.MetaData;
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
}
