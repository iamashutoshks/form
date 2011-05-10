/**
 * This file Copyright (c) 2008-2011 Magnolia International
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
package info.magnolia.module.form.controls;

import java.util.Iterator;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.core.Path;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.module.admininterface.UUIDSaveHandler;

/**
 * Save handler taking care of saving controls implementing MultiValueControl interface.
 * 
 *
 */
public class MultiControlSaveHandler extends UUIDSaveHandler {

    protected void processString(Content node, String name, int type,
            int encoding, String[] values, String valueStr)
            throws PathNotFoundException, RepositoryException,
            AccessDeniedException {
        Object control = this.getDialog().getSub(name);
        // only handle special multi selects
        if (control instanceof MultiValueControl) {
            try {
                // delete existing content
                node.delete(name);
            }
            catch (PathNotFoundException e) {
                // node does not exist, yet (ok)
            }
            Content filters = node.createContent(name, ItemType.CONTENTNODE);
            new JSONArray();
            JSONArray json = JSONArray.fromObject(valueStr);
            for (int i = 0; i < json.size(); i++) {
                Content content = filters.createContent(Path.getUniqueLabel(filters, "00"), ItemType.CONTENTNODE);
                JSONObject value = json.getJSONObject(i);
                Iterator<String> iter = value.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    content.createNodeData(key, value.get(key));
                }
            }
        } else {
            super.processString(node, name, type, encoding, values, valueStr);
        }
    }
}
