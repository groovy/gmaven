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

/**
 * Tests for the {@link org.codehaus.groovy.maven.runtime.support.stubgen.parser.DynamicTokens} class.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DynamicTokensTest
    extends TestCase
{
    public void testDiscovery() throws Exception {
        DynamicTokens tokens = new DynamicTokens(ExampleTokenTypes.class);

        assertEquals(1, tokens.value("EOF"));
        assertEquals("EOF", tokens.name(1));

        try {
            tokens.value("NOT_A_TOKEN");
            fail();
        }
        catch (IllegalArgumentException expected) {}

        try {
            tokens.name(-1);
            fail();
        }
        catch (IllegalArgumentException expected) {}
    }
}