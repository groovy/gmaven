/*
 * Copyright (C) 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.maven.gossip;

import org.slf4j.spi.LocationAwareLogger;

/**
 * Gossip logging level container.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class Level
{
    public static final int TRACE_ID = LocationAwareLogger.TRACE_INT;

    public static final Level TRACE = new Level("TRACE", TRACE_ID);

    public static final int DEBUG_ID = LocationAwareLogger.DEBUG_INT;

    public static final Level DEBUG = new Level("DEBUG", DEBUG_ID);

    public static final int INFO_ID = LocationAwareLogger.INFO_INT;

    public static final Level INFO = new Level("INFO", INFO_ID);

    public static final int WARN_ID = LocationAwareLogger.WARN_INT;

    public static final Level WARN = new Level("WARN", WARN_ID);

    public static final int ERROR_ID = LocationAwareLogger.ERROR_INT;

    public static final Level ERROR = new Level("ERROR", ERROR_ID);

    private static final Level[] ALL = {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR
    };

    public final String label;

    public final int id;

    private Level(final String label, final int id) {
        assert label != null;

        this.label = label;
        this.id = id;
    }

    public String toString() {
        return label;
    }

    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Level level = (Level) obj;

        return id == level.id;
    }

    public int hashCode() {
        return id;
    }

    //
    // Conversion
    //

    public static Level forLabel(final String label) {
        assert label != null;

        String tmp = label.trim().toUpperCase();

        for (int i=0; i< ALL.length; i++) {
            if (ALL[i].label.equals(tmp)) {
                return ALL[i];
            }
        }

        throw new IllegalArgumentException("Unknown level label: " + label);
    }

    public static Level forId(final int id) {
        for (int i=0; i< ALL.length; i++) {
            if (ALL[i].id == id) {
                return ALL[i];
            }
        }

        throw new IllegalArgumentException("Unknown level id: " + id);
    }
}