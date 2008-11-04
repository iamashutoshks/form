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

import org.apache.log4j.Level;

/**
 * Defines custom logging level for forms data
 * @author tmiyar
 *
 */
public class LoggingLevel extends Level{

    private static final long serialVersionUID = 1L;

    public static final LoggingLevel FORM_TRAIL = new LoggingLevel(98, "FORM_TRAIL", 0);

    protected LoggingLevel(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);

    }

    public static Level toLevel(String sArg) {
      return FORM_TRAIL;
    }

    public static Level toLevel(int val) {
      return FORM_TRAIL;
    }

}
