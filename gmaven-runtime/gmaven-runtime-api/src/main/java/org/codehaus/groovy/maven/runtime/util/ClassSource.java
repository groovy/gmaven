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

package org.codehaus.groovy.maven.runtime.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Container which provides defails about a Groovy scripts source.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ClassSource
{
    public final URL url;

    public final File file;

    public final Body body;

    private ClassSource(final URL url, final File file, final Body body) {
        this.url = url;
        this.file = file;
        this.body = body;
    }

    public ClassSource(final URL url) {
        this(url, null, null);
    }

    public ClassSource(final File file) {
        this(null, file, null);
    }

    public ClassSource(final Body body) {
        this(null, null, body);
    }

    public String toString() {
        return "ClassSource" +
                "[ url=" + url +
                ", file=" + file +
                ", body=" + body +
                " ]";
    }

    //
    // Body
    //

    public static class Body
    {
        public final String name;

        public final String codeBase;

        public final InputStream input;

        public Body(final String name, final String codeBase, final InputStream input) {
            assert name != null;
            assert codeBase != null;
            assert input != null;
            
            this.name = name;
            this.codeBase = codeBase;
            this.input = input;
        }

        public Body(final String source) {
            this("script" + System.currentTimeMillis() + ".groovy",
                 "/groovy/script",
                 new ByteArrayInputStream(source.getBytes()));
        }

        public String toString() {
            return "Body" +
                    "[ name=" + name +
                    ", codeBase=" + codeBase +
                    ", input=" + input +
                    " ]";
        }
    }

    //
    // Helpers
    //

    public static ClassSource forValue(final String source) {
        assert source != null;

        // First try and parse the source as a URL
        try {
            return new ClassSource(new URL(source));
        }
        catch (MalformedURLException ignore) {}

        // Then as a File
        File file = new File(source);
        if (file.exists()) {
            return new ClassSource(file);
        }

        // Else its a body
        return new ClassSource(new ClassSource.Body(source));
    }
}
