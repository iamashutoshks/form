/**
 * This file Copyright (c) 2012 Magnolia International
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import info.magnolia.cms.core.Content;
import info.magnolia.cms.gui.dialog.Dialog;
import info.magnolia.cms.gui.dialog.DialogControl;
import info.magnolia.cms.gui.dialog.DialogControlImpl;
import info.magnolia.cms.gui.dialog.DialogFactory;
import info.magnolia.cms.gui.i18n.DefaultI18nAuthoringSupport;
import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.context.MgnlContext;
import info.magnolia.importexport.DataTransporter;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.MockContent;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DialogRadioSwitchTest.
 */
public class DialogRadioSwitchTest extends RepositoryTestCase {

    private DialogRadioSwitch drs;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Content storageNode;
    private Content configNode;
    private Dialog dialog;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        InputStream xmlStream = this.getClass().getClassLoader().getResourceAsStream("website.demo-project.members-area.registration.content.0.xml");
        DataTransporter.importXmlStream(
                xmlStream,
                "website",
                "/root",
                "name matters only when importing a file that needs XSL transformation",
                false,
                ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW,
                true,
                true);

        xmlStream = this.getClass().getClassLoader().getResourceAsStream("config.modules.form.dialogs.form.tabConfirmEmail.confirmContentType.xml");
        DataTransporter.importXmlStream(
                xmlStream,
                "config",
                "/root",
                "name matters only when importing a file that needs XSL transformation",
                false,
                ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW,
                true,
                true);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        storageNode = MgnlContext.getHierarchyManager("website").getContent("/root/0");
        configNode = MgnlContext.getHierarchyManager("config").getContent("/root/confirmContentType");
        dialog = mock(Dialog.class);

        MgnlContext.setLocale(new Locale("de"));

        DefaultI18nContentSupport i18nSupport = new DefaultI18nContentSupport();
        i18nSupport.setEnabled(true);
        ComponentsTestUtil.setInstance(I18nContentSupport.class, i18nSupport);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testDialogRadioSwitchIsI18nIzed() throws RepositoryException, IOException {
        // GIVEN
        drs = new DialogRadioSwitch();

        Class<? extends DialogControl> clazz = DummyDialogControl.class;
        DialogFactory.registerDialog("fckEdit", (Class<DialogControl>) clazz);
        DialogFactory.registerDialog("edit", (Class<DialogControl>) clazz);

        drs.init(request, response, storageNode, configNode);

        dialog.addSub(drs);

        DialogControlImpl tab = mock(DialogControlImpl.class);
        List<DialogControlImpl> tabs = new ArrayList<DialogControlImpl>();
        tabs.add(tab);
        List<DialogControlImpl> subs = new ArrayList<DialogControlImpl>();
        subs.add(drs);

        when(request.getMethod()).thenReturn("GET");
        when(tab.getSubs()).thenReturn(subs);
        when(dialog.getConfigValue("locale", null)).thenReturn("de");
        when(dialog.getStorageNode()).thenReturn(new MockContent("test"));
        when(dialog.getSubs()).thenReturn(tabs);

        DefaultI18nAuthoringSupport i18n = new DefaultI18nAuthoringSupport();
        i18n.setEnabled(true);
        i18n.i18nIze(dialog);

        StringWriter out = new StringWriter();

        // WHEN
        drs.drawHtml(out);

        // THEN
        assertTrue(StringUtils.contains(out.toString(), "<input type=\"radio\" name=\"confirmContentType_de\""));
    }

    @Test
    public void testJavascriptForEditSelectIsRendered() throws Exception {
        // GIVEN
        drs = new DialogRadioSwitch();

        Class<? extends DialogControl> clazz = DummyDialogControl.class;
        DialogFactory.registerDialog("fckEdit", (Class<DialogControl>) clazz);
        DialogFactory.registerDialog("edit", (Class<DialogControl>) clazz);

        drs.init(request, response, storageNode, configNode);

        dialog.addSub(drs);

        DialogControlImpl tab = mock(DialogControlImpl.class);
        List<DialogControlImpl> tabs = new ArrayList<DialogControlImpl>();
        tabs.add(tab);
        List<DialogControlImpl> subs = new ArrayList<DialogControlImpl>();
        subs.add(drs);

        when(request.getMethod()).thenReturn("GET");

        StringWriter out = new StringWriter();

        // WHEN
        drs.drawHtml(out);

        // THEN
        assertTrue(StringUtils.contains(out.toString(), "<script type=\"text/javascript\">var func = function() { mgnl.form.FormDialogs.onSelectionChanged('confirmContentType','text', null) };MgnlDHTMLUtil.addOnLoad(func);</script>"));
    }

    @Test
    public void testElementsHaveCorrectOnClickJavascriptFunctions() throws Exception {
        // GIVEN
        drs = new DialogRadioSwitch();

        Class<? extends DialogControl> clazz = DummyDialogControl.class;
        DialogFactory.registerDialog("fckEdit", (Class<DialogControl>) clazz);
        DialogFactory.registerDialog("edit", (Class<DialogControl>) clazz);

        drs.init(request, response, storageNode, configNode);

        dialog.addSub(drs);

        DialogControlImpl tab = mock(DialogControlImpl.class);
        List<DialogControlImpl> tabs = new ArrayList<DialogControlImpl>();
        tabs.add(tab);
        List<DialogControlImpl> subs = new ArrayList<DialogControlImpl>();
        subs.add(drs);

        when(request.getMethod()).thenReturn("GET");
        when(tab.getSubs()).thenReturn(subs);
        when(dialog.getConfigValue("locale", null)).thenReturn("de");
        when(dialog.getStorageNode()).thenReturn(new MockContent("test"));
        when(dialog.getSubs()).thenReturn(tabs);

        DefaultI18nAuthoringSupport i18n = new DefaultI18nAuthoringSupport();
        i18n.setEnabled(true);
        i18n.i18nIze(dialog);

        StringWriter out = new StringWriter();

        // WHEN
        drs.drawHtml(out);

        // THEN
        assertTrue(StringUtils.contains(out.toString(), "onclick=\"mgnl.form.FormDialogs.onSelectionChanged('confirmContentType_de','text', '_de')\""));
    }

    public static class DummyDialogControl extends DialogControlImpl {

        @Override
        public void init(HttpServletRequest request, HttpServletResponse response, Content websiteNode, Content configNode) throws RepositoryException {
        }

        @Override
        public void drawHtml(Writer out) throws IOException {
        }

    }

}
