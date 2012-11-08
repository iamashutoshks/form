/**
 * This file Copyright (c) 2010-2012 Magnolia International
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
package info.magnolia.module.form.setup.for1_2_1;

import java.util.Collection;
import java.util.Iterator;

import javax.jcr.RepositoryException;

import info.magnolia.cms.core.Content;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.module.form.setup.UpdateAllDialogDefinitions;

/**
 * Visits all dialog definitions in the form module and removes some i18n flags where they are not appropriate (e.g. checkbox, select, radio, hidden)).
 *
 * @author fgrilli
 * @version $Revision: $ ($Author: $)
 */
public class UpdateDialogDefinitionsFor1_2_1 extends UpdateAllDialogDefinitions{

    public UpdateDialogDefinitionsFor1_2_1() {
        super("Update all dialog definitions", "Removes i18n flag from some controls (e.g. checkbox, select, radio, hidden) in form dialogs");
    }

    @Override
    protected void updateDialogDefinition(InstallContext ctx, Content dialogDefinition) throws RepositoryException, TaskExecutionException {
        Collection<Content> dialogDefinitions = dialogDefinition.getChildren();

        Iterator<Content> iterator = dialogDefinitions.iterator();

        while(iterator.hasNext()){

           String nodeName = iterator.next().getName();
           Content node = dialogDefinition.getContent(nodeName);
           Collection<Content> subDefinitions = node.getChildren();

           Iterator<Content> subiterator = subDefinitions.iterator();

           while(subiterator.hasNext()){

               String subNodeName = subiterator.next().getName();
               Content subNode = node.getContent(subNodeName);
               if(removeI18nFlag(subNode)){
                   ctx.info("Removing i18n flag for : " + subNode.getHandle());
                   subNode.getNodeData("i18n").delete();
               }
           }
        }
    }

    private boolean removeI18nFlag(final Content node) throws RepositoryException{
        if(!node.hasNodeData("i18n")){
            return false;
        }
        final String name = node.getName();
        boolean toDelete = "controlName".equals(name) || "formName".equals(name) || "maxLength".equals(name) || "requiredSymbol".equals(name) || name.contains("MailFrom") || name.contains("MailTo");

        if(toDelete){
            return true;
        }

        if(!node.hasNodeData("controlType")){
            return false;
        }
        final String controlType = node.getNodeData("controlType").getString();
        toDelete = "checkboxSwitch".equals(controlType) || "radio".equals(controlType) || "select".equals(controlType) || "hidden".equals(controlType) || "uuidLink".equals(controlType);
        return toDelete;
    }
}
