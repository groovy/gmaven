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

import java.net.URL;

/**
 * Pseudo-enum for Groovy/Java source type.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class SourceType
{
    public static final SourceType GROOVY = new SourceType("GROOVY", 0);

    public static final String GROOVY_EXT = ".groovy";

    public static final String GROOVY_NAME = "GROOVY";

    public static final int GROOVY_CODE = 0;

    public static final SourceType JAVA = new SourceType("JAVA", 1);

    public static final String JAVA_EXT = ".java";

    public static final String JAVA_NAME = "JAVA";

    public static final int JAVA_CODE = 1;

    public static final String SOURCE_TYPE_TAG = "source-type";

    public final String name;

    public final int code;

    private SourceType(final String name, final int code) {
        this.name = name;
        this.code = code;
    }

    public String toString() {
        return name;
    }

    public static SourceType forURL(final URL url) {
        String name = url.getFile().toLowerCase();

        if (name.endsWith(GROOVY_EXT)) {
            return GROOVY;
        }
        if (name.endsWith(JAVA_EXT)) {
            return JAVA;
        }
        else {
            throw new IllegalArgumentException("Unable to determine source type from URL: " + url);
        }
    }

    public static SourceType forName(final String name) {
        if (name.equals(GROOVY_NAME)) {
            return GROOVY;
        }
        if (name.equals(JAVA_NAME)) {
            return JAVA;
        }
        else {
            throw new IllegalArgumentException("Unable to determine source type from name: " + name);
        }
    }

    public static SourceType forCode(final int code) {
        if (code == GROOVY_CODE) {
            return GROOVY;
        }
        if (code == JAVA_CODE) {
            return JAVA;
        }
        else {
            throw new IllegalArgumentException("Unable to determine source type from code: " + code);
        }
    }
}