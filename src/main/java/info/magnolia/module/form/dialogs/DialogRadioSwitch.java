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
package info.magnolia.module.form.dialogs;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.core.ItemType;
import info.magnolia.cms.gui.control.Button;
import info.magnolia.cms.gui.control.ButtonSet;
import info.magnolia.cms.gui.control.ControlImpl;
import info.magnolia.cms.gui.dialog.DialogBox;
import info.magnolia.cms.gui.dialog.DialogButtonSet;
import info.magnolia.cms.gui.dialog.DialogControl;
import info.magnolia.cms.gui.dialog.DialogControlImpl;
import info.magnolia.cms.gui.dialog.DialogFactory;
import info.magnolia.cms.gui.misc.CssConstants;
import info.magnolia.cms.security.AccessDeniedException;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.cms.util.NodeDataUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This dialog will switch between edit or fckEdit control depending on the
 * mail contenttype selected.
 *
 * @version $Id$
 */
public class DialogRadioSwitch extends DialogControlImpl {

    private static final String ORIGINAL_NAME = "originalName";
    private static final Logger log = LoggerFactory.getLogger(DialogRadioSwitch.class);
    private DialogBox box;
    private List<Button> selectOptions = new ArrayList<Button>();
    private Map<String, List<DialogControlImpl>> optionsSubs = new HashMap<String, List<DialogControlImpl>>();

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, Content storageNode, Content configNode) throws RepositoryException {
        super.init(request, response, storageNode, configNode);
        selectOptions = loadRadioOptions(configNode);
    }

    private List<Button> loadRadioOptions(Content configNode) throws RepositoryException, PathNotFoundException, AccessDeniedException {
        List<Button> selectOptions = new ArrayList<Button>();
        if(configNode.hasContent("options")){
            Collection<Content> options = configNode.getContent("options").getChildren(ItemType.CONTENTNODE);
            for (Content option : options) {
                Button optionButton = new Button(this.getName(), option.getName());
                optionButton.setLabel(this.getMessage(NodeDataUtil.getString(option, "label")));
                selectOptions.add(optionButton);

                loadSub(option);
            }
        }
        return selectOptions;
    }

    private void loadSub(Content option) {
        List<DialogControlImpl> subs = new ArrayList<DialogControlImpl>();
        optionsSubs.put(option.getName(), subs);
        try {
            if(option.hasContent("controls")) {
                Collection<Content> controls = ContentUtil.getContent(option, "controls").getChildren(ItemType.CONTENTNODE);
                for (Object c : controls) {
                    Content controlNodeConfig = (Content) c;
                    try {
                        DialogControl control = DialogFactory.loadDialog(this.getRequest(), this.getResponse(), this.getStorageNode(), controlNodeConfig);

                        String name = ((DialogControlImpl) control).getName();
                        ((DialogControlImpl) control).setName(this.getName() + name);

                        this.addSub(control);
                        subs.add((DialogControlImpl)control);

                    } catch (RepositoryException e) {
                        // ignore
                        log.debug(e.getMessage(), e);
                    }
                }
            }
        } catch (RepositoryException e) {
            // ignore
            log.debug(e.getMessage(), e);
        }

    }

    @Override
    public void drawHtmlPreSubs(Writer out) throws IOException {
        drawRadio(out);
    }

    @Override
    protected void drawSubs(Writer out) throws IOException {
        String aName = this.getName();
        //TODO: would it not be more systematic to set (and keep) locale in separate variable and to be able to still retrieve unlocalized name? Maybe for 5.0 ...
        String originalName = this.getConfigValue(ORIGINAL_NAME);
        String locale = "";
        if (!StringUtils.isBlank(originalName)) {
            locale = StringUtils.substringAfter(aName, originalName);
            if (locale.startsWith("_")) {
                aName = originalName;
            } else {
                locale = "";
            }
        }

        for(Button option: selectOptions) {
            String style = "style=\"display:none;\"";
            out.write("<div id=\"" + aName + option.getValue() + locale + "_radioswich_div\" " + style + " >");
            out.write("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed\" >");
            out.write("<col width=\"200\" /><col />");

            Iterator<DialogControlImpl> it = optionsSubs.get(option.getValue()).iterator();

            while (it.hasNext()) {
                DialogControlImpl control = it.next();
                control.drawHtml(out);
            }
            out.write("</table></div>");
        }
    }

    public void drawRadio(Writer out) throws IOException {

        for (Button option : selectOptions) {
            option.setOnclick("mgnl.form.FormDialogs.onSelectionChanged('" + this.getName() + "','" + option.getValue() + "')");
            option.setName(this.getName());
        }

        box = new DialogButtonSet();
        try {
            box.init(this.getRequest(), this.getResponse(), null, null);
        } catch (RepositoryException e) {
            //ignore
        }
        box.setLabel(this.getMessage(this.getLabel()));
        box.setSaveInfo(true);

        ButtonSet control;
        // radio
        control = new ButtonSet(this.getName(), this.getValue());

        control.setButtonType(ControlImpl.BUTTONTYPE_RADIO);

        control.setCssClass(this.getConfigValue("cssClass", CssConstants.CSSCLASS_BUTTONSETBUTTON));

        control.setSaveInfo(true);

        control.setType(PropertyType.TYPENAME_STRING);

        control.setButtonHtmlPre("<tr><td class=\"" + CssConstants.CSSCLASS_BUTTONSETBUTTON + "\">");
        control.setButtonHtmlInter("</td><td class=\"" + CssConstants.CSSCLASS_BUTTONSETLABEL + "\">");
        control.setButtonHtmlPost("</td></tr>");

        int cols = selectOptions.size();
        if (cols > 1) {
            control.setHtmlPre(control.getHtmlPre() + "<tr>");
            control.setHtmlPost("</tr>" + control.getHtmlPost());
            int item = 1;
            int itemsPerCol = (int) Math.ceil(selectOptions.size() / (double) cols);
            for (Object selectOption : selectOptions) {
                Button b = (Button) selectOption;
                if (item == 1) {
                    b.setHtmlPre("<td><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" >"
                            + control.getButtonHtmlPre());
                }
                if (item == itemsPerCol) {
                    b.setHtmlPost(control.getButtonHtmlPost() + "</table></td><td class=\""
                            + CssConstants.CSSCLASS_BUTTONSETINTERCOL
                            + "\"></td>");
                    item = 1;
                } else {
                    item++;
                }
            }
            // very last button: close table
            int lastIndex = selectOptions.size() - 1;
            // avoid ArrayIndexOutOfBoundsException, but should not happen
            if (lastIndex > -1) {
                selectOptions.get(lastIndex).setHtmlPost(control.getButtonHtmlPost() + "</table>");
            }
        }

        int width = 100;
        if(cols < 5) {
            width = cols * 20;
        }
        control.setHtmlPre("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"" + width + "%\">");

        control.setButtons(selectOptions);
        box.drawHtmlPre(out);
        out.write(control.getHtml());

    }

    @Override
    public void setName(String s) {
        // on name update made due to i18n, relay updated names also to all subs
        if (!StringUtils.isBlank(s)) {
            String originalName = super.getName();
            String locale = StringUtils.substringAfter(s, originalName);
            if (!StringUtils.isBlank(originalName) && locale.startsWith("_")) {
                // keep the original name for later
                this.setConfig(ORIGINAL_NAME, originalName);
                // also update all subs since they have been set all by now
                List<DialogControlImpl> subs = this.getSubs();
                for (DialogControlImpl sub: subs) {
                    sub.setName(sub.getName()+locale);
                }
            }
        }
        super.setName(s);
    }

}
