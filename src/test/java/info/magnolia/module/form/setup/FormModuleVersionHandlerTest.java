/**
 * This file Copyright (c) 2003-2015 Magnolia International
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
        this.setupConfigProperty(FormModuleVersionHandler.HONEY_POT_VALIDATION_FIELD_PATH, "controlType", "static");
        this.setupConfigProperty(FormModuleVersionHandler.HONEY_POT_VALIDATION_FIELD_PATH, "value", "empty");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("1.4.10"));

        // THEN
        assertEquals("hidden", session.getProperty(FormModuleVersionHandler.HONEY_POT_VALIDATION_FIELD_PATH + "controlType").getString());
        assertTrue(session.propertyExists(FormModuleVersionHandler.HONEY_POT_VALIDATION_FIELD_PATH + "defaultValue"));
        assertEquals("empty", session.getProperty(FormModuleVersionHandler.HONEY_POT_VALIDATION_FIELD_PATH + "defaultValue").getString());
    }

    @Test
    public void updateFrom1411() throws Exception {
        // GIVEN
        this.setupConfigNode(FormModuleVersionHandler.HONEY_POT_VALIDATION_FIELD_PATH);

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("1.4.11"));

        // THEN
        assertTrue(session.propertyExists(FormModuleVersionHandler.HONEY_POT_VALIDATION_FIELD_PATH + "defaultValue"));
        assertEquals("empty", session.getProperty(FormModuleVersionHandler.HONEY_POT_VALIDATION_FIELD_PATH + "defaultValue").getString());
    }
}
