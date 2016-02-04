/**
 * This file Copyright (c) 2010-2016 Magnolia International
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
package info.magnolia.module.form.setup.for1_2;

import java.util.Collection;
import java.util.Iterator;

import javax.jcr.RepositoryException;

import info.magnolia.cms.core.Content;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.form.setup.UpdateAllDialogDefinitions;

import org.apache.commons.lang.StringUtils;

/**
 * Visits all dialog definitions in form module and adds i18n flag.
 * 
 * @author ochytil
 * @version $Revision: $ ($Author: $)
 */
public class UpdateDialogDefinitionFor1_2 extends UpdateAllDialogDefinitions{

    public UpdateDialogDefinitionFor1_2() {
        super("Update all dialog definitions", "I18n property added in form dialogs");
    }

    @Override
    protected void updateDialogDefinition(InstallContext ctx,
            Content dialogDefinition) throws RepositoryException,
            TaskExecutionException {
        Collection<Content> dialogDefinitions = dialogDefinition.getChildren();
        
        Iterator iterator = dialogDefinitions.iterator();
        
        while(iterator.hasNext()){

           String nodeName = iterator.next().toString();
           nodeName = StringUtils.substringAfterLast(nodeName, "/");
           nodeName = StringUtils.substringBeforeLast(nodeName, "[");

           Content node = dialogDefinition.getContent(nodeName);
           Collection<Content> subDefinitions = node.getChildren();

           Iterator subiterator = subDefinitions.iterator();

           while(subiterator.hasNext()){

               String subNodeName = subiterator.next().toString();
               subNodeName = StringUtils.substringAfterLast(subNodeName, "/");
               subNodeName = StringUtils.substringBeforeLast(subNodeName, "[");

               Content subNode = node.getContent(subNodeName);

               if(!subNode.hasNodeData("i18n")){
                   subNode.setNodeData("i18n", true);
               }else{
                   ctx.info("i18n flag already in location: " + subNode.getHandle());
               }
           }
        }
    }
}
