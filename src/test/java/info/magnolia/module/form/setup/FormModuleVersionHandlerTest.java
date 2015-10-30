/**
 * This file Copyright (c) 2013-2015 Magnolia International
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

import static info.magnolia.test.hamcrest.NodeMatchers.hasNode;
import static info.magnolia.test.hamcrest.NodeMatchers.hasProperty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.form.templates.components.FormFieldTemplate;
import info.magnolia.module.form.validators.FileUploadValidator;
import info.magnolia.module.model.Version;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.dialog.setup.migration.ControlMigratorsRegistry;
import info.magnolia.ui.form.field.definition.MultiValueFieldDefinition;
import info.magnolia.ui.form.field.definition.StaticFieldDefinition;
import info.magnolia.ui.form.field.definition.SwitchableFieldDefinition;
import info.magnolia.ui.form.field.definition.TwinColSelectFieldDefinition;

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
        this.setupConfigNode("/modules/form/templates/components/formPassword");
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
                "/META-INF/magnolia/core.xml"
        );
    }

    @Test
    public void updateTo21DialogActionsUseKeysAsLabels() throws Exception {
        // GIVEN
        List<String> dialogs = Arrays.asList(new String[]{"form", "formCondition", "formEdit", "formFile", "formGroupEdit", "formGroupEditItem", "formGroupFields", "formHidden", "formHoneypot", "formSelection", "formStep", "formSubmit", "formSummary"});
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
        this.bootstrapOldConfiguration();

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("1.4.5"));

        // THEN
        assertTrue(session.propertyExists("/modules/form/dialogs/form/form/tabs/tabConfirmEmail/fields/sendConfirmation/defaultValue"));
        assertTrue(session.propertyExists("/modules/form/dialogs/form/form/tabs/tabMain/fields/displayStepNavigation/defaultValue"));
        assertTrue(session.propertyExists("/modules/form/dialogs/form/form/tabs/tabSubmit/fields/trackMail/defaultValue"));
        assertTrue(session.propertyExists("/modules/form/dialogs/formEdit/form/tabs/tabMain/fields/mandatory/defaultValue"));
        assertTrue(session.propertyExists("/modules/form/dialogs/formGroupEditItem/form/tabs/tabMain/fields/mandatory/defaultValue"));
        assertTrue(session.propertyExists("/modules/form/dialogs/formSelection/form/tabs/tabMain/fields/horizontal/defaultValue"));
        assertTrue(session.propertyExists("/modules/form/dialogs/formSelection/form/tabs/tabMain/fields/mandatory/defaultValue"));
        assertTrue(session.propertyExists("/modules/form/dialogs/formSelection/form/tabs/tabMain/fields/multiple/defaultValue"));
        assertTrue(session.propertyExists("/modules/form/dialogs/formSummary/form/tabs/tabMain/fields/onlyLast/defaultValue"));
    }

    private void bootstrapOldConfiguration() throws Exception {
        this.bootstrapSingleResource("/mgnl-bootstrap/oldConfiguration/dialogs/config.modules.form.dialogs.form.xml");
        this.bootstrapSingleResource("/mgnl-bootstrap/oldConfiguration/dialogs/config.modules.form.dialogs.formEdit.xml");
        this.bootstrapSingleResource("/mgnl-bootstrap/oldConfiguration/dialogs/config.modules.form.dialogs.formGroupEditItem.xml");
        this.bootstrapSingleResource("/mgnl-bootstrap/oldConfiguration/dialogs/config.modules.form.dialogs.formSelection.xml");
        this.bootstrapSingleResource("/mgnl-bootstrap/oldConfiguration/dialogs/config.modules.form.dialogs.formSummary.xml");
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

    @Test
    public void updateFrom223() throws Exception {
        // GIVEN
        this.setupConfigProperty("/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "class", "info.magnolia.ui.form.field.definition.StaticFieldDefinition");
        this.setupConfigProperty("/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "value", "empty");
        this.setupConfigProperty("/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "buttonLabel", "dialog.form.edit.tabMain.validation.buttonLabel");
        this.setupConfigProperty("/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "path", "/modules/form/config/validators");
        this.setupConfigProperty("/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation", "repository", "config");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.3"));

        // THEN
        Node validationNode = session.getNode("/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation");
        assertThat(validationNode, hasProperty("class", "info.magnolia.ui.form.field.definition.HiddenFieldDefinition"));
        assertThat(validationNode, hasProperty("defaultValue", "empty"));
        assertFalse(validationNode.hasProperty("buttonLabel"));
        assertFalse(validationNode.hasProperty("path"));
        assertFalse(validationNode.hasProperty("repository"));
    }

    @Test
    public void updateFrom225() throws Exception {
        // GIVEN
        final String[] fields = new String[]{"formEdit", "formGroupEditItem"};
        for (String field : fields) {
            String pathToField = String.format("/modules/form/dialogs/%s/form/tabs/tabMain/fields/validation", field);
            this.setupConfigProperty(pathToField, "class", "info.magnolia.ui.form.field.definition.SelectFieldDefinition");
            this.setupConfigProperty(pathToField, "buttonLabel", "test");
        }
        this.setupConfigNode("/modules/form/config/validators/none");
        Session websiteSession = MgnlContext.getJCRSession(RepositoryConstants.WEBSITE);
        Node validationWebsiteNode = NodeUtil.createPath(websiteSession.getRootNode(), "fields/0", NodeTypes.ContentNode.NAME);
        validationWebsiteNode.setProperty("validation", "email");
        validationWebsiteNode.setProperty("mgnl:template", "form:components/formEdit");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.5"));

        // THEN
        for (String field : fields) {
            Node validationNode = session.getNode(String.format("/modules/form/dialogs/%s/form/tabs/tabMain/fields/validation", field));
            assertThat(validationNode, hasProperty("class", "info.magnolia.ui.form.field.definition.TwinColSelectFieldDefinition"));
            assertThat(validationNode, hasProperty("leftColumnCaption", "dialog.form.edit.tabMain.validation.leftColumnCaption"));
            assertThat(validationNode, hasProperty("rightColumnCaption", "dialog.form.edit.tabMain.validation.rightColumnCaption"));
            assertFalse(validationNode.hasProperty("buttonLabel"));
        }
        assertTrue(validationWebsiteNode.getProperty("validation").isMultiple());
        assertFalse(session.getRootNode().hasNode("modules/form/config/validators/none"));
    }

    @Test
    public void testCorrectConfigurationEmailExpression() throws Exception {
        // GIVEN
        this.setupConfigNode("/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation");
        Node node = NodeUtil.createPath(session.getRootNode(), FormModuleVersionHandler.PATH_VALIDATORS_EMAIL, NodeTypes.ContentNode.NAME);
        node.setProperty("expression", "^\\S+@\\S+$");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.4"));

        // THEN
        assertThat(session.getNode(FormModuleVersionHandler.PATH_VALIDATORS_EMAIL), hasProperty("expression", "(^([a-zA-Z0-9_\\.\\-+])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$)"));
        assertEquals("empty", session.getNode("/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation").getProperty("defaultValue").getString());

    }

    @Test
    public void updateFrom226() throws Exception {
        // GIVEN

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.6"));

        // THEN
        assertThat(session.getNode("/modules/form/templates/components/formPassword"), hasProperty("escapeHtml", false));
    }

    @Test
    public void updateFrom227() throws Exception {
        // GIVEN
        setupConfigProperty("/modules/form/config/validators/empty", "class", "info.magnolia.module.form.validators.ValidateExpression");
        setupConfigProperty("/modules/form/config/validators/email", "class", "info.magnolia.module.form.validators.ValidateExpression");
        setupConfigProperty("/modules/form/config/validators/number", "class", "info.magnolia.module.form.validators.ValidateExpression");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.7"));

        // THEN
        assertThat(session.getNode("/modules/form/config/validators/empty"), hasProperty("class", "info.magnolia.module.form.validators.RegexValidator"));
        assertThat(session.getNode("/modules/form/config/validators/email"), hasProperty("class", "info.magnolia.module.form.validators.RegexValidator"));
        assertThat(session.getNode("/modules/form/config/validators/number"), hasProperty("class", "info.magnolia.module.form.validators.RegexValidator"));
    }

    @Test
    public void updateFrom228() throws Exception {
        // GIVEN
        String existingParentPath = "/modules/form/dialogs/formSubmit/form/tabs/tabMain/fields";
        String newNodePath = existingParentPath + "/" + "cancelButtonText";
        setupConfigNode(existingParentPath);

        // WHEN
        InstallContext installContext = executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.8"));
        Session installSession = installContext.getJCRSession(RepositoryConstants.CONFIG);

        // THEN
        assertTrue(installSession.nodeExists(newNodePath));
        assertThat(session.getNode(newNodePath), hasProperty("class", "info.magnolia.ui.form.field.definition.TextFieldDefinition"));
        assertThat(session.getNode(newNodePath), hasProperty("description", "dialog.form.submit.tabMain.cancelButtonText.description"));
        assertThat(session.getNode(newNodePath), hasProperty("label", "dialog.form.submit.tabMain.cancelButtonText.label"));
    }

    @Test
    public void updateFrom229() throws Exception {
        // GIVEN
        this.setupConfigNode("/modules/form/dialogs/formHoneypot/form/tabs/tabMain/fields/validation");
        Node node = NodeUtil.createPath(session.getRootNode(), FormModuleVersionHandler.PATH_VALIDATORS_EMAIL, NodeTypes.ContentNode.NAME);
        node.setProperty("expression", "(^$|^\\S+@\\S+$)");

        // WHEN
        InstallContext installContext = executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.9"));
        Session installSession = installContext.getJCRSession(RepositoryConstants.CONFIG);

        // THEN
        assertThat(session.getNode(FormModuleVersionHandler.PATH_VALIDATORS_EMAIL), hasProperty("expression", "(^([a-zA-Z0-9_\\.\\-+])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$)"));
    }

    /**
     * Tests the update tasks when updating from 2.2.12 to 2.2.13.
     */
    @Test
    public void updateFrom2212() throws Exception {
        // GIVEN
        setupConfigNode("/modules/form/config/validators");
        setupConfigNode("/modules/form/dialogs/formFile/form/tabs/tabMain/fields");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.12"));

        // THEN
        assertTrue(session.nodeExists("/modules/form/dialogs/formFile/form/tabs/tabMain/fields/validation"));
        assertTrue(session.nodeExists("/modules/form/config/validators/fileUpload"));
        assertThat(session.getNode("/modules/form/dialogs/formFile/form/tabs/tabMain/fields/validation"), hasProperty("class", TwinColSelectFieldDefinition.class.getName()));
        assertThat(session.getNode("/modules/form/config/validators/fileUpload"), hasProperty("class", FileUploadValidator.class.getName()));
    }

    @Test
    public void updateFrom2213() throws Exception {
        // GIVEN
        Node formPasswordTemplate = session.getNode("/modules/form/templates/components/formPassword");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.2.13"));

        // THEN
        assertThat(formPasswordTemplate, hasProperty("class", FormFieldTemplate.class.getName()));
    }

    @Test
    public void updateFrom231() throws Exception {
        // GIVEN
        Node templates = session.getNode("/modules/form/templates/components/");
        NodeUtil.createPath(templates, "components/formGroupFields/areas/fields/availableComponents/", NodeTypes.ContentNode.NAME);
        Node dialogs = NodeUtil.createPath(session.getRootNode(), "/modules/form/dialogs/formEdit", NodeTypes.ContentNode.NAME).getParent();

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("2.3.1"));

        // THEN
        assertThat(templates, hasNode("formNumber"));
        assertThat(templates, hasNode("formGroupFields/areas/fields/availableComponents/formNumber"));
        assertThat(dialogs, hasNode("formNumber"));
        assertThat(dialogs, hasNode("formEdit/form/tabs/tabAdvanced"));
    }

}
