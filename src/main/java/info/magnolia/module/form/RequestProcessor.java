/**
 * This file Copyright (c) 2007-2008 Magnolia International
 * Ltd.  (http://www.magnolia.info). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia.info/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.module.form;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.mail.MgnlMailFactory;
import info.magnolia.cms.mail.templates.MgnlEmail;
import info.magnolia.cms.mail.templates.impl.FreemarkerEmail;
import info.magnolia.context.MgnlContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import java.util.Iterator;

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

        String body = content.getNodeData("contactMailBody").getString("<br />");
        String from = content.getNodeData("contactMailFrom").getString();
        String subject = content.getNodeData("contactMailSubject").getString();
        String to = content.getNodeData("contactMailTo").getString();

        sendMail(body, from, subject, to);
    }

    protected void sendConfirmationEMail(Content content) throws Exception {

        if(content.getNodeData("sendConfirmation").getBoolean()) {
            String body = content.getNodeData("confirmMailBody").getString("<br />");
            String from = content.getNodeData("confirmMailFrom").getString();
            String subject = content.getNodeData("confirmMailSubject").getString();
            String to = content.getNodeData("confirmMailTo").getString();

            sendMail(body, from, subject, to);
        }
    }

    protected void sendMail(String body, String from, String subject, String to) throws Exception {
        MgnlEmail email;

        Map parameters = getParameters();
        email = MgnlMailFactory.getInstance().getEmailFromType("freemarker");
        email.setBody(body, parameters);
        ((FreemarkerEmail)email).setFrom(from, parameters);
        ((FreemarkerEmail)email).setSubject(subject, parameters);
        ((FreemarkerEmail)email).setToList(to, parameters);
        email.setParameters(parameters);

        MgnlMailFactory.getInstance().getEmailHandler().prepareAndSendMail(email);

    }

    private Map getParameters() {
        //getparametermap does not work as expected
        Map params = MgnlContext.getParameters();
        Map result = new HashMap();
        Iterator i = (Iterator) params.entrySet().iterator();

        while(i.hasNext()) {
            Entry pairs = (Entry)i.next();
            String key = (String)pairs.getKey();
            result.put(key, StringUtils.replace(StringUtils.join(MgnlContext.getParameterValues(key), "__"), "\r\n", "<br />"));
        }
        return result;
    }

    protected void logFormParameters(Content content) {

        if(content.getNodeData("trackMail").getBoolean()) {

            Map params = getParameters();
            Iterator i = (Iterator) params.entrySet().iterator();
            StringBuffer buf = new StringBuffer();
            while(i.hasNext()) {
                Entry pairs = (Entry)i.next();
                buf.append(" " + pairs.getKey() + " : " + pairs.getValue() + ",");
            }
            org.apache.log4j.Logger.getLogger(this.getLoggerName()).log(
                    LoggingLevel.FORM_TRAIL, StringUtils.remove(StringUtils.chomp(buf.toString(), ","), "<br />"));
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
