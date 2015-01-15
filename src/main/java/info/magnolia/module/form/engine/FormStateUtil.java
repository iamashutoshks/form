/**
 * This file Copyright (c) 2010-2015 Magnolia International
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
import info.magnolia.link.LinkUtil;
import info.magnolia.repository.RepositoryConstants;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Utility class for storing FormState in session and getting the form state token from a request.
 */
public class FormStateUtil {

    public static final String FORM_TOKEN_PARAMETER_NAME = "mgnlFormToken";
    private static final String FORM_STATE_ATTRIBUTE_PREFIX = FormEngine.class.getName() + "-formState-";

    public static FormState createAndSetFormState() {

        // This can fail if the response is already committed
        HttpSession session = MgnlContext.getWebContext().getRequest().getSession();

        FormState formState = newFormState();
        session.setAttribute(FORM_STATE_ATTRIBUTE_PREFIX + formState.getToken(), formState);
        return formState;
    }

    public static FormState newFormState() {
        FormState formState = new FormState();
        formState.setToken(RandomStringUtils.randomAlphanumeric(32));
        return formState;
    }

    public static String getFormStateToken() throws FormStateTokenMissingException {
        String formStateToken = MgnlContext.getParameter(FORM_TOKEN_PARAMETER_NAME);
        if (formStateToken == null) {
            throw new FormStateTokenMissingException();
        }
        return formStateToken;
    }

    public static FormState getFormState(String formStateToken) throws NoSuchFormStateException {

        // This can fail if the response is already committed
        HttpSession session = MgnlContext.getWebContext().getRequest().getSession();

        FormState formState = (FormState) session.getAttribute(FORM_STATE_ATTRIBUTE_PREFIX + formStateToken);
        if (formState == null) {
            throw new NoSuchFormStateException(formStateToken);
        }

        return formState;
    }

    public static void destroyFormState(FormState formState) {

        // This can fail if the response is already committed
        HttpSession session = MgnlContext.getWebContext().getRequest().getSession();

        session.removeAttribute(FORM_STATE_ATTRIBUTE_PREFIX + formState.getToken());
    }

    public static void sendRedirect(String uuid) throws RepositoryException, IOException {
        sendRedirect(uuid, RepositoryConstants.WEBSITE);
    }

    public static void sendRedirect(String uuid, String workspace) throws RepositoryException, IOException {
        // be sure that workspace is not null
        workspace = StringUtils.isBlank(workspace) ? RepositoryConstants.WEBSITE : workspace;
        String link = LinkUtil.createAbsoluteLink(workspace, uuid);
        ((WebContext) MgnlContext.getInstance()).getResponse().sendRedirect(link);
    }


    public static void sendRedirectWithToken(String uuid, String formExecutionToken) throws RepositoryException, IOException {
        sendRedirectWithTokenAndParameters(uuid, formExecutionToken, null);
    }

    public static void sendRedirectWithToken(String uuid, String formExecutionToken, String workspace) throws RepositoryException, IOException {
        sendRedirectWithTokenAndParameters(uuid, formExecutionToken, null, workspace);
    }

    public static void sendRedirectWithTokenAndParameters(String uuid, String formExecutionToken, Map<String, String> parameters) throws RepositoryException, IOException {
        sendRedirectWithTokenAndParameters(uuid, formExecutionToken, parameters, RepositoryConstants.WEBSITE);
    }

    public static void sendRedirectWithTokenAndParameters(String uuid, String formExecutionToken, Map<String, String> parameters, String workspace) throws RepositoryException, IOException {
        // be sure that workspace is not null
        workspace = StringUtils.isBlank(workspace) ? RepositoryConstants.WEBSITE : workspace;
        String link = LinkUtil.createAbsoluteLink(workspace, uuid);
        String[] paths = link.split("/");
        for (int i = 0; i < paths.length; i++) {
            link = link.replace(paths[i], URLEncoder.encode(paths[i], "UTF-8"));
        }

        link += "?" + FORM_TOKEN_PARAMETER_NAME + "=" + formExecutionToken;
        if (parameters != null) {
            for (Entry<String, String> param : parameters.entrySet()) {
                link = link + "&" + param.getKey() + "=" + param.getValue();
            }
        }
        ((WebContext) MgnlContext.getInstance()).getResponse().sendRedirect(link);
    }
}
