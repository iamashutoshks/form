/**
 * This file Copyright (c) 2013-2017 Magnolia International
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
package info.magnolia.module.form.templates.components;

import static org.junit.Assert.*;

import info.magnolia.cms.i18n.DefaultI18nContentSupport;
import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.cms.i18n.LocaleDefinition;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.wrapper.HTMLEscapingNodeWrapper;
import info.magnolia.module.form.templates.components.multistep.StartStepFormEngine;
import info.magnolia.rendering.template.TemplateAvailability;
import info.magnolia.rendering.template.configured.ConfiguredTemplateAvailability;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.RepositoryTestCase;
import info.magnolia.test.mock.jcr.MockNode;

import java.util.Locale;

import javax.jcr.Node;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for AbstractFormEngine.
 */
public class AbstractFormEngineTest extends RepositoryTestCase {

    private final DefaultI18nContentSupport i18n = new DefaultI18nContentSupport();
    private final Locale anotherLocale = new Locale("de");
    private final Locale defaultLocale = new Locale("en");

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        LocaleDefinition anotherLocaleDefinition = new LocaleDefinition();
        anotherLocaleDefinition.setLanguage("de");
        anotherLocaleDefinition.setLocale(anotherLocale);
        anotherLocaleDefinition.setEnabled(true);

        LocaleDefinition defaultLocaleDefinition = new LocaleDefinition();
        defaultLocaleDefinition.setLanguage("en");
        defaultLocaleDefinition.setLocale(anotherLocale);
        defaultLocaleDefinition.setEnabled(true);

        i18n.addLocale(defaultLocaleDefinition);
        i18n.addLocale(defaultLocaleDefinition);
        i18n.setEnabled(true);
        i18n.setDefaultLocale(defaultLocale);
        ComponentsTestUtil.setInstance(I18nContentSupport.class, i18n);
        ComponentsTestUtil.setImplementation(TemplateAvailability.class, ConfiguredTemplateAvailability.class);
    }

    @Test
    public void testUnwrapConfigurationNode() throws Exception {
        // GIVEN
        Node content = new HTMLEscapingNodeWrapper(new MockNode(), true);
        content.setProperty("property", "<");
        AbstractFormEngine engine = new StartStepFormEngine(content, new FormParagraph(), null);
        // WHEN
        Node configurationNode = engine.getConfigurationNode();
        // THEN
        assertFalse(configurationNode instanceof HTMLEscapingNodeWrapper);
        assertEquals("<", PropertyUtil.getString(configurationNode, "property"));
    }

    @Test
    public void testConfigPropertiesAreI18nAwareTest() throws Exception {
        // GIVEN
        Node configNode = new MockNode("configNode");
        configNode.setProperty("subject", "subjectEn");
        configNode.setProperty("subject_de", "subjectDe");
        AbstractFormEngine engine = new StartStepFormEngine(configNode, new FormParagraph(), null);
        // WHEN
        Node configurationNode = engine.getConfigurationNode();
        // THEN
        assertFalse(configurationNode instanceof HTMLEscapingNodeWrapper);
        assertEquals("subjectEn", PropertyUtil.getString(configurationNode, "subject"));

        // GIVEN
        i18n.setLocale(anotherLocale);
        // WHEN
        configurationNode = engine.getConfigurationNode();
        // THEN
        assertFalse(configurationNode instanceof HTMLEscapingNodeWrapper);
        assertEquals("subjectDe", PropertyUtil.getString(configurationNode, "subject"));
    }
}
