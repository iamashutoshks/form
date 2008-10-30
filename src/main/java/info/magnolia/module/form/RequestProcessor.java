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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class RequestProcessor {
  public static void sendMail(Map parameters, Content content) {
    MgnlEmail email;
    try {

       StringBuffer body = new StringBuffer();

       parameters.put("all", body.toString());
       email = MgnlMailFactory.getInstance().getEmailFromTemplate("/modules/form/mailTemplates/contactFormMail", parameters);
        email.setToList(content.getNodeData("To").getString());
        email.setCcList("");
        email.setBccList("");
        email.setReplyToList("");
        email.setFrom();
        email.setSubject("kk");
        MgnlMailFactory.getInstance().getEmailHandler().prepareAndSendMail(email);
    }
    catch (Exception e) {
        // you may want to warn the user redirecting him to a different page...
e.printStackTrace();
    }

  }
}
