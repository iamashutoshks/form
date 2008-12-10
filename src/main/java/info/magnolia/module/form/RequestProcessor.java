/**
 * This file Copyright (c) 2008 Magnolia International
 * Ltd.  (http://www.magnolia.info). All rights reserved.
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
 * is available at http://www.magnolia.info/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.form;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.mail.MailModule;
import info.magnolia.cms.mail.templates.MgnlEmail;
import info.magnolia.cms.mail.util.MailUtil;
import info.magnolia.context.MgnlContext;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author tmiyar
 *
 */
public class RequestProcessor {

    private String name;

    private String loggerName;

    public void process(Content content) throws Exception {
        sendContactEMail(content);
        sendConfirmationEMail(content);
        logFormParameters(content);
    }

    protected void sendContactEMail(Content content) throws Exception {

        String body = content.getNodeData("contactMailBody")
                .getString();
        String from = content.getNodeData("contactMailFrom").getString();
        String subject = content.getNodeData("contactMailSubject").getString();
        String to = content.getNodeData("contactMailTo").getString();
        String contentType = content.getNodeData("contentType").getString();

        sendMail(body, from, subject, to, contentType);
    }

    protected void sendConfirmationEMail(Content content) throws Exception {

        if (content.getNodeData("sendConfirmation").getBoolean()) {
            String body = content.getNodeData("confirmMailBody").getString();
            String from = content.getNodeData("confirmMailFrom").getString();
            String subject = content.getNodeData("confirmMailSubject")
                    .getString();
            String to = content.getNodeData("confirmMailTo").getString();
            String contentType = content.getNodeData("contentType").getString();

            sendMail(body, from, subject, to, contentType);
        }
    }

    protected void sendMail(String body, String from, String subject, String to, String contentType)
            throws Exception {
        MgnlEmail email;

        Map parameters = getParameters();
        List attachments = MailUtil.createAttachmentList();
        email = MailModule.getInstance().getFactory().getEmailFromType(parameters, "freemarker", contentType, attachments);
        email.setFrom(from);
        email.setSubject(subject);
        email.setToList(to);
        email.setBody(body);

        MailModule.getInstance().getHandler().sendMail(email);

    }


    protected Map getParameters() {
        // getparametermap does not work as expected
        Map params = MgnlContext.getParameters();
        Map result = new HashMap();
        Iterator i = (Iterator) params.entrySet().iterator();

        while (i.hasNext()) {
            Entry pairs = (Entry) i.next();
            String key = (String) pairs.getKey();
            result.put(key, StringUtils.join(MgnlContext
                    .getParameterValues(key), "__"));

        }
        return result;
    }

    protected void logFormParameters(Content content) {

        if (content.getNodeData("trackMail").getBoolean()) {

            Map params = getParameters();
            MailUtil.logMail(params, loggerName);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

}
