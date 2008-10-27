/**
 * This file Copyright (c) 2007-2008 Magnolia International
 * Ltd.  (http://www.magnolia.info). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia.info/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.form.paragraphs;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

import info.magnolia.cms.beans.config.ContentRepository;
import info.magnolia.cms.beans.config.Paragraph;
import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.baukasten.util.BKUtil;

/**
 *
 * @author tmiyar
 *
 */
public class FormParagraph extends Paragraph {


    public String resolveLink(Content content){
        String link = content.getNodeData("link").getString();
        String linkType = content.getNodeData("linkType").getString();

        if(StringUtils.equals(linkType, "external")) {
            if(!link.startsWith("http://")){
                link = "http://"+link;
            }
            return link;
        } else {
            HierarchyManager hm = MgnlContext.getHierarchyManager(ContentRepository.WEBSITE);
            try {
                return BKUtil.createLink(hm.getContentByUUID(link));
            } catch (RepositoryException e) {
                return "Can't resolve node with uuid " + link;
            }
        }
    }
}