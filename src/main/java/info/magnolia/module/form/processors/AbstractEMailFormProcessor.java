/**
 * This file Copyright (c) 2008-2016 Magnolia International
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

import info.magnolia.module.mail.MailModule;
import info.magnolia.module.mail.MgnlMailFactory;
import info.magnolia.module.mail.templates.MailAttachment;
import info.magnolia.module.mail.templates.MgnlEmail;
import info.magnolia.module.mail.util.MailUtil;
import info.magnolia.util.EscapeUtil;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;


/**
 * Base implementation for FormProcessors that send mail.
 *
 * @author tmiyar
 */
public abstract class AbstractEMailFormProcessor extends AbstractFormProcessor {

    /**
     * Checks if contentType is "text" type and return parameters with unescaped characters.
     */
    protected final Map resolveParameters(String contentType, Map<String, Object> parameters) {
        if (!"text".equals(contentType)) {
            return parameters;
        }

        return Maps.transformEntries(parameters, new Maps.EntryTransformer<String, Object, Object>() {
            @Override
            public Object transformEntry(String key, Object value) {
                return (value instanceof String) ? EscapeUtil.unescapeXss((String) value) : value;
            }
        });
    }

    protected void sendMail(String body, String from, String subject, String to, String contentType, Map<String, Object> parameters) throws Exception {

        MgnlMailFactory mgnlMailFactory = MailModule.getInstance().getFactory();

        List<MailAttachment> attachments = MailUtil.createAttachmentList();
        Map<String, Object> resolvedParameters = resolveParameters(contentType, parameters);

        MgnlEmail email = mgnlMailFactory.getEmailFromType(resolvedParameters, "freemarker", contentType, attachments);
        email.setFrom(from);
        email.setSubject(subject);
        email.setToList(to);
        email.setBody(body);

        MailModule.getInstance().getHandler().sendMail(email);
    }
}
