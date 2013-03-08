/**
 * This file Copyright (c) 2008-2012 Magnolia International
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
package info.magnolia.module.form.processors;

import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.mail.MailModule;
import info.magnolia.module.mail.MgnlMailFactory;
import info.magnolia.module.mail.templates.MailAttachment;
import info.magnolia.module.mail.templates.MgnlEmail;
import info.magnolia.module.mail.util.MailUtil;

import java.util.List;
import java.util.Map;

import javax.jcr.Node;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends a confirmation mail, any files submitted are sent as attachments.
 *
 * @author tmiyar
 */
public class SendConfirmationEMailProcessor extends AbstractEMailFormProcessor {

    private static final Logger log = LoggerFactory.getLogger(SendConfirmationEMailProcessor.class);

    @Override
    public void internalProcess(Node content, Map<String, Object> parameters) throws FormProcessorFailedException {
        try {
            if ( PropertyUtil.getBoolean(content, "sendConfirmation", false)) {
                if(StringUtils.equals(PropertyUtil.getString(content,"confirmContentType"), "page")){
                    parameters.put("templateFile", PropertyUtil.getString(content, "confirmContentTypepage"));

                    MgnlMailFactory factory = MailModule.getInstance().getFactory();
                    List<MailAttachment> attachments = MailUtil.createAttachmentList();
                    MgnlEmail email = factory.getEmailFromType(parameters, "magnolia", "html", attachments);

                    email.setFrom(PropertyUtil.getString(content, "confirmMailFrom"));
                    email.setSubject(PropertyUtil.getString(content, "confirmMailSubject"));
                    email.setToList(PropertyUtil.getString(content, "confirmMailTo"));
                    email.setBodyFromResourceFile();
                    factory.getEmailHandler().sendMail(email);
                }else{
                    String from = PropertyUtil.getString(content, "confirmMailFrom");
                    String subject = PropertyUtil.getString(content,"confirmMailSubject");
                    String to = PropertyUtil.getString(content,"confirmMailTo");
                    String contentType = PropertyUtil.getString(content,"confirmContentType");
                    //For control edit and new control DialogRadioSwitch, keep old param for compatibility
                    String body = PropertyUtil.getString(content, "confirmMailBody", PropertyUtil.getString(content, "confirmContentType"+contentType));

                    sendMail(body, from, subject, to, contentType, parameters);
                }
            }
        } catch (Exception e) {
            log.error("Confirmation email", e);
            throw new FormProcessorFailedException("SendConfirmationEMailProcessor.errorMessage");
        }
    }
}
