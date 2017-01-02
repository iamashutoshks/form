/**
 * This file Copyright (c) 2010-2017 Magnolia International
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
package info.magnolia.module.form.engine;

import java.io.IOException;
import javax.jcr.RepositoryException;

import info.magnolia.cms.core.Content;
import info.magnolia.module.templating.RenderingModel;

/**
 * Used to redirect to a page with the form state token, effectively continuing the form on another page.
 */
public class RedirectWithTokenView implements View {

    private String uuid;
    private String token;

    public RedirectWithTokenView(Content content, String token) {
        this.uuid = content.getUUID();
        this.token = token;
    }

    public RedirectWithTokenView(String uuid, String token) {
        this.uuid = uuid;
        this.token = token;
    }

    public String execute() throws RepositoryException, IOException {
        FormStateUtil.sendRedirectWithToken(uuid, token);
        return RenderingModel.SKIP_RENDERING;
    }
}
