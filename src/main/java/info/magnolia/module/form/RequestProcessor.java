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

import java.util.Map;

/**
 *
 * @author tmiyar
 *
 */
public class RequestProcessor {

    private String name;

    public void process(Map parameters, Content content) throws Exception {
        sendContactEMail(parameters, content);
        sendConfirmationEMail(parameters, content);
    }

    protected void sendContactEMail(Map parameters, Content content) throws Exception {

        String body = content.getNodeData("contactMailBody").getString("<br />");
        String from = content.getNodeData("contactMailFrom").getString();
        String subject = content.getNodeData("contactMailSubject").getString();
        String to = content.getNodeData("contactMailTo").getString();

        sendMail(parameters, body, from, subject, to);
    }

    protected void sendConfirmationEMail(Map parameters, Content content) throws Exception {

        if(content.getNodeData("sendConfirmation").getBoolean()) {
            String body = content.getNodeData("confirmMailBody").getString("<br />");
            String from = content.getNodeData("confirmMailFrom").getString();
            String subject = content.getNodeData("confirmMailSubject").getString();
            String to = content.getNodeData("confirmMailTo").getString();

            sendMail(parameters, body, from, subject, to);
        }
    }

    protected void sendMail(Map parameters, String body, String from, String subject, String to) throws Exception {
        MgnlEmail email;

        email = MgnlMailFactory.getInstance().getEmailFromType("freemarker");
        email.setBody(body, parameters);
        ((FreemarkerEmail)email).setFrom(from, parameters);
        ((FreemarkerEmail)email).setSubject(subject, parameters);
        ((FreemarkerEmail)email).setToList(to, parameters);
        email.setParameters(parameters);

        MgnlMailFactory.getInstance().getEmailHandler().prepareAndSendMail(email);


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
