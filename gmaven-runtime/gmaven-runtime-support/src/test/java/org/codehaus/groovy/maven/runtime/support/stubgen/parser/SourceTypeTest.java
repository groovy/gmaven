/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import junit.framework.TestCase;

import java.net.URL;

/**
 * Tests for the {@link org.codehaus.groovy.maven.runtime.support.stubgen.parser.SourceType} class.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SourceTypeTest
    extends TestCase
{
    public void testForURL() throws Exception {
        assertEquals(SourceType.GROOVY, SourceType.forURL(new URL("file:/foo.GrooVY")));
        
        assertEquals(SourceType.JAVA, SourceType.forURL(new URL("file:/foo.jaVA")));

        try {
            SourceType.forURL(new URL("file:/foo.cpp"));
            fail();
        }
        catch (IllegalArgumentException expected) {}
    }
}