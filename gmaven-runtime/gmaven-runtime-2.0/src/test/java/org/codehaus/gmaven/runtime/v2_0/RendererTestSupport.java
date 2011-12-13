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

package org.codehaus.gmaven.runtime.v2_0;

import junit.framework.TestCase;
import org.codehaus.gmaven.runtime.StubCompiler;

import java.io.File;
import java.net.URL;

/**
 * Support for rendering tests.
 *
 * @version $Id: RendererTestSupport.java 52 2009-11-22 10:32:14Z user57 $
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class RendererTestSupport
    extends TestCase
{
    private StubCompiler compiler;

    protected void setUp() throws Exception {
        compiler = new StubCompilerFeature().createInternal();

        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        File dir = new File(path).getParentFile();
        dir = new File(dir, "test-generated-sources");
        System.out.println(dir);
        compiler.setTargetDirectory(dir);
    }

    protected void tearDown() throws Exception {
        compiler = null;
    }

    protected void chew(final URL input) throws Exception {
        assertNotNull(input);

        compiler.add(input);
        compiler.compile();
    }

    protected void chew(final String resource) throws Exception {
        chew(getClass().getResource(resource));
    }
}