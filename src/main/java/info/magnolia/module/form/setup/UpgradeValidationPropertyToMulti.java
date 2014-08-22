/**
 * This file Copyright (c) 2014 Magnolia International
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

import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.NodeVisitor;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractTask;
import info.magnolia.module.delta.TaskExecutionException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add to validation property multi=true attribute.
 */
public class UpgradeValidationPropertyToMulti extends AbstractTask {
    private static final Logger log = LoggerFactory.getLogger(UpdateConfirmHtmlTypeToCodeTask.class);
    NodeVisitor nodeVisitor = new NodeVisitor() {
        @Override
        public void visit(Node node) throws RepositoryException {
            Node field;
            if (node.getName().equals("fields")) {
                NodeIterator nodeIterator = node.getNodes();
                while (nodeIterator.hasNext()) {
                    field = nodeIterator.nextNode();
                    if (field.hasProperty("validation") && !field.getProperty("validation").isMultiple()) {
                        Value[] values=new Value[] {field.getProperty("validation").getValue()};
                        field.getProperty("validation").remove();
                        field.setProperty("validation", values);
                    }
                }
            }
        }
    };

    public UpgradeValidationPropertyToMulti(String taskName, String taskDescription) {
        super("Add to validation property multi=true attribute", "");
    }

    @Override
    public void execute(InstallContext ctx) throws TaskExecutionException {
        try {
            Session session = ctx.getJCRSession("website");
            Node rootNode = session.getRootNode();
            NodeUtil.visit(rootNode, nodeVisitor);
            session.save();
        } catch (PathNotFoundException e) {
            log.error("Switch of confirm mail type from html to code failed due to missing node(s): ", e);
        } catch (RepositoryException e) {
            throw new TaskExecutionException(e.getMessage(), e);
        }
    }
}
