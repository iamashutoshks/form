/**
 * This file Copyright (c) 2012-2013 Magnolia International
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

import info.magnolia.context.MgnlContext;
import info.magnolia.context.WebContext;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.rendering.model.RenderingModel;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;

/**
 * Used to redirect to a page with the form state token and all url parameters,
 * effectively continuing the form on another page.
 */
public class RedirectWithTokenAndParametersView implements View {

    private static final long serialVersionUID = -5473123248902107120L;
    private final String uuid;
    private final String token;

    public RedirectWithTokenAndParametersView(Node content, String token) {
        this.uuid = NodeUtil.getNodeIdentifierIfPossible(content);
        this.token = token;
    }

    public RedirectWithTokenAndParametersView(String uuid, String token) {
        this.uuid = uuid;
        this.token = token;
    }

    @Override
    public String execute() throws RepositoryException, IOException {
        Map<String, String> parameters = new LinkedHashMap<String, String>();
        String queryString = ((WebContext) MgnlContext.getInstance()).getRequest().getQueryString();
        String[] params = StringUtils.split(queryString, "&");

        if (params != null) {
            for (String param : params) {
                if (StringUtils.startsWith(param, "mgnlForm")) {
                    continue;
                }
                String key = StringUtils.substringBefore(param, "=");
                String value = StringUtils.substringAfter(param, "=");
                parameters.put(key, value);
            }
        }

        FormStateUtil.sendRedirectWithTokenAndParameters(this.uuid, this.token, parameters);
        return RenderingModel.SKIP_RENDERING;
    }

}
