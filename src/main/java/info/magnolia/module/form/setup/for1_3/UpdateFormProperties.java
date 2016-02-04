/**
 * This file Copyright (c) 2012-2016 Magnolia International
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
package info.magnolia.module.form.setup.for1_3;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.jcr.RepositoryException;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.HierarchyManager;
import info.magnolia.cms.core.ItemType;
import info.magnolia.context.MgnlContext;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

/**
 * Task to update 'contactMailBody' and 'confirmMailBody' properties to actual naming - contentType[text||html] and confirmContentType[text||html]. 
 * 
 * @version $id$
 * 
 */
public class UpdateFormProperties extends AbstractRepositoryTask{

    public UpdateFormProperties(String name, String description) {
        super("Update form properties.", "Update property name for 'contactMailBody' and 'confirmMailBody'.");
    }

    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException {
        HierarchyManager hm = MgnlContext.getHierarchyManager("website");
        Content root = hm.getRoot();
        searchAndReplaceFormProperties(root);
        hm.save();
    }
    
    public void searchAndReplaceFormProperties(Content content) throws RepositoryException{
        if (content.hasNodeData("confirmMailBody")){
            String value = content.getNodeData("confirmMailBody").getString();
            content.setNodeData("confirmContentType" + content.getNodeData("confirmContentType").getString(), value);
            content.getNodeData("confirmMailBody").delete();
        }
        if (content.hasNodeData("contactMailBody")){
            String value = content.getNodeData("contactMailBody").getString();
            content.setNodeData("contentType" + content.getNodeData("contentType").getString(), value);
            content.getNodeData("contactMailBody").delete();
        }
        Collection<Content> children = new HashSet<Content>();
        if(content.hasChildren(ItemType.CONTENT.toString())){
            children.addAll(content.getChildren(ItemType.CONTENT));
        }
        if(content.hasChildren(ItemType.CONTENTNODE.toString())){
            children.addAll(content.getChildren(ItemType.CONTENTNODE));
        }
        Iterator<Content> it = children.iterator();
        while(it.hasNext()){
            Content node = it.next();
            searchAndReplaceFormProperties(node);
        }
    }
}
