package info.magnolia.module.form.setup;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import info.magnolia.context.MgnlContext;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.model.Version;
import info.magnolia.repository.RepositoryConstants;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;


public class FormModuleVersionHandlerTest extends ModuleVersionHandlerTestCase {

    private Session session;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        session = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
    }

    @Override
    protected String getModuleDescriptorPath() {
        return "/META-INF/magnolia/form.xml";
    }

    @Override
    protected ModuleVersionHandler newModuleVersionHandlerForTests() {
        return new FormModuleVersionHandler();
    }

    @Override
    protected List<String> getModuleDescriptorPathsForTests() {
        return Arrays.asList(
                "/META-INF/magnolia/core.xml",
                "/META-INF/magnolia/templating.xml",
                "/META-INF/magnolia/magnolia-4-5-migration.xml",
                "/META-INF/magnolia/mail.xml",
                "/META-INF/magnolia/rendering.xml",
                "/META-INF/magnolia/admininterface.xml",
                "/META-INF/magnolia/groovy.xml"
        );
    }

    @Test
    public void updateFrom1410() throws Exception {
        // GIVEN
        this.setupConfigProperty("/modules/form/dialogs/formHoneypot/tabMain/validation", "controlType", "static");
        this.setupConfigProperty("/modules/form/dialogs/formHoneypot/tabMain/validation", "value", "empty");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("1.4.10"));

        // THEN
        assertEquals("hidden", session.getProperty("/modules/form/dialogs/formHoneypot/tabMain/validation/controlType").getString());
        assertTrue(session.propertyExists("/modules/form/dialogs/formHoneypot/tabMain/validation/defaultValue"));
        assertEquals("empty", session.getProperty("/modules/form/dialogs/formHoneypot/tabMain/validation/defaultValue").getString());
    }
}
