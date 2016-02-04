/**
 * This file Copyright (c) 2014-2016 Magnolia International
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
package info.magnolia.module.form.setup;

import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.NodeVisitor;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.lang.StringUtils;

/**
 * Convert 'validation' property to multi-valued property.
 */
public class ChangeValidationToMultiValuedPropertyTask extends AbstractTask {

    private List<String> listOfTemplates;
    private static String fields = "fields";
    private static String validation = "validation";

    private NodeVisitor nodeVisitor = new NodeVisitor() {
        @Override
        public void visit(Node node) throws RepositoryException {
            Node field;
            if (node.getName().equals(fields)) {
                NodeIterator nodeIterator = node.getNodes();
                while (nodeIterator.hasNext()) {
                    field = nodeIterator.nextNode();
                    if (checkNode(field)) {
                        Value value = field.getProperty(validation).getValue();
                        field.getProperty(validation).remove();
                        field.setProperty(validation, new Value[] { value });
                    }
                }
            }
        }
    };

    public ChangeValidationToMultiValuedPropertyTask(String taskDescription, List<String> listOfTemplates) {
        super("Change validation property from single type to multi valued property", taskDescription);
        this.listOfTemplates = listOfTemplates;
    }

    @Override
    public void execute(InstallContext ctx) throws TaskExecutionException {
        try {
            Session session = ctx.getJCRSession(RepositoryConstants.WEBSITE);
            Node rootNode = session.getRootNode();
            NodeUtil.visit(rootNode, nodeVisitor);
        } catch (RepositoryException e) {
            throw new TaskExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Checks if a node is eligible for being changed to a multi-valued property.
     */
    private boolean checkNode(Node node) throws RepositoryException {
        if (!node.hasProperty(NodeTypes.Renderable.TEMPLATE) || !node.hasProperty(validation)) {
            return false;
        }
        String templateName = node.getProperty(NodeTypes.Renderable.TEMPLATE).getString();
        if (!listOfTemplates.contains(templateName)) {
            return false;
        }
        if (node.getProperty(validation).isMultiple()) {
            return false;
        }

        String stringValue = node.getProperty(validation).getString();
        if (StringUtils.equals("none", stringValue)) {
            // Remove property if set to none
            node.getProperty(validation).remove();
            return false;
        }

        return true;
    }

}
