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

package org.codehaus.groovy.maven.runtime.support.stubgen.parser;

/**
 * Thrown to indicate a parsing failure.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ParseException
    extends Exception
{
    private Location location;

    public ParseException(final Throwable cause) {
        super(cause);
    }

    public ParseException(final Throwable cause, final Location location) {
        super(cause);

        assert location != null;
        
        this.location = location;
    }

    public ParseException(final String message, final Location location) {
        super(message);

        assert location != null;

        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public String getMessage() {
        if (location == null) {
            return super.getMessage();
        }

        return super.getMessage() + " - " + location;
    }

    public static class Location
    {
        public final int line;

        public final int column;

        public final String fileName;

        public Location(final int line, final int column, final String fileName) {
            assert line > 0;
            assert column > 0;
            assert fileName != null;

            this.line = line;
            this.column = column;
            this.fileName = fileName;
        }

        public String toString() {
            return fileName + "[" + line + ":" + column + "]";
        }
    }
}