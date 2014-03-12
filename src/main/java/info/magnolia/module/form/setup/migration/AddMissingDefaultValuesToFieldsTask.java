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
package info.magnolia.module.form.setup.migration;

import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task .
 */
public class AddMissingDefaultValuesToFieldsTask extends AbstractRepositoryTask {

    private static final Logger log = LoggerFactory.getLogger(AddMissingDefaultValuesToFieldsTask.class);
    private final List<String> fields;
    private final String value;
    private final String propertyName;

    public AddMissingDefaultValuesToFieldsTask(List<String> fields, String propertyName, String value) {
        super("Add missing proprties to dialogs fields", String.format("Adds properties '%s=%s' to fields: %s", propertyName, value, fields));
        this.fields = fields;
        this.propertyName = propertyName;
        this.value = value;
    }

    @Override
    protected void doExecute(InstallContext installContext) throws RepositoryException, TaskExecutionException {
        Session config = installContext.getConfigJCRSession();

        for (String field : fields) {
            if (!config.nodeExists(field)) {
                installContext.warn(String.format("Can't set property '%s=%s' to nonexisting field: '%s'.", propertyName, value, field));
                continue;
            }
            Node fieldNode = config.getNode(field);
            if (!fieldNode.hasProperty(propertyName)) {
                fieldNode.setProperty(propertyName, value);
            }
        }

    }

}
