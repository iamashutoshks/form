/**
 * This file Copyright (c) 2013 Magnolia International
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
package info.magnolia.module.form.setup;

import static org.junit.Assert.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.model.Version;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.dialog.setup.migration.ControlMigratorsRegistry;
import info.magnolia.ui.form.field.definition.MultiValueFieldDefinition;
import info.magnolia.ui.form.field.definition.StaticFieldDefinition;
import info.magnolia.ui.form.field.definition.SwitchableFieldDefinition;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link FormModuleVersionHandler}.
 */
public class FormModuleVersionHandlerTest extends ModuleVersionHandlerTestCase {

    private Session session;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        // for 2.2.2 update:
        this.setupConfigProperty("/modules/form/dialogs/form/form/tabs/tabConfirmEmail/fields/confirmContentType", "class", StaticFieldDefinition.class.getCanonicalName());
        this.setupConfigProperty("/modules/form/dialogs/formCondition/form/tabs/tabMain/fields/condition", "class", StaticFieldDefinition.class.getCanonicalName());
    }

    @Override
    protected String getModuleDescriptorPath() {
        return "/META-INF/magnolia/form.xml";
    }

    @Override
    protected ModuleVersionHandler newModuleVersionHandlerForTests() {
        return new FormModuleVersionHandler(Components.getComponent(ControlMigratorsRegistry.class));
    }

    @Override
    protected List<String> getModuleDescriptorPathsForTests() {
        return Arrays.asList(
                "/META-INF/magnolia/core.xml",
                "/META-INF/magnolia/templating.xml",
                "/META-INF/magnolia/rendering.xml",
                "/META-INF/magnolia/admininterface-legacy.xml",
                "/META-INF/magnolia/ui-admincentral.xml",
                "/META-INF/magnolia/ui-framework.xml",
                "/META-INF/magnolia/imaging.xml",
                "/META-INF/magnolia/activation.xml",
                "/META-INF/magnolia/mail.xml"
                );
    }

    @Test
    public void updateTo21DialogActionsUseKeysAsLabels() throws Exception {
        // GIVEN
        List<String> dialogs = Arrays.asList(new String[] { "form", "formCondition", "formEdit", "formFile", "formGroupEdit", "formGroupEditItem", "formGroupFields", "formHidden", "formHoneypot", "formSelection", "formStep", "formSubmit", "formSummary" });
        for (String dialog : dialogs) {
            Node commit = NodeUtil.createPath(session.getRootNode(), "modules/form/dialogs/" + dialog + "/actions/commit", NodeTypes.ContentNode.NAME);
            PropertyUtil.setProperty(commit, "label", "save changes");
            Node cancel = NodeUtil.createPath(session.getRootNode(), "modules/form/dialogs/" + dialog + "/actions/cancel", NodeTypes.ContentNode.NAME);
            PropertyUtil.setProperty(cancel, "label", "cancel");
        }

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.0.2"));

        // THEN
        for (String dialog : dialogs) {
            Node commit = session.getRootNode().getNode("modules/form/dialogs/" + dialog + "/actions/commit");
            Node cancel = session.getRootNode().getNode("modules/form/dialogs/" + dialog + "/actions/cancel");
            assertFalse("Commit action label still hardcoded in dialog [" + dialog + "]", commit.hasProperty("label"));
            assertFalse("Cancel action label still hardcoded in dialog [" + dialog + "]", cancel.hasProperty("label"));
        }
    }

    @Test
    public void updateFrom145() throws Exception {
        // GIVEN
        this.setupConfigNode("/modules/form/dialogs/form/form/tabs/tabConfirmEmail/fields/confirmMailType");
        this.setupConfigNode("/modules/form/dialogs/form/form/tabs/tabConfirmEmail/fields/confirmContentType");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("1.4.5"));

        // THEN no errors
    }

    @Test
    public void updateFrom221() throws Exception {
        // GIVEN

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.1"));

        // THEN
        assertEquals(SwitchableFieldDefinition.class.getCanonicalName(), session.getProperty("/modules/form/dialogs/form/form/tabs/tabConfirmEmail/fields/confirmContentType/class").getString());
        assertEquals(MultiValueFieldDefinition.class.getCanonicalName(), session.getProperty("/modules/form/dialogs/formCondition/form/tabs/tabMain/fields/condition/class").getString());
    }

}
