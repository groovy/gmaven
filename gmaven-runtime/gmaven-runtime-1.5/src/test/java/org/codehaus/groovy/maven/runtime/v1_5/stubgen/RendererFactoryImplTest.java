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

package org.codehaus.groovy.maven.runtime.v1_5.stubgen;

import junit.framework.TestCase;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ModelFactory;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.SourceDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.render.Renderer;
import org.codehaus.groovy.maven.runtime.support.stubgen.render.RendererFactory;

import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

/**
 * Tests for the {@link RendererFactoryImpl} class.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class RendererFactoryImplTest
    extends TestCase
{
    private ModelFactory modelFactory;

    private RendererFactory rendererFactory;

    protected void setUp() throws Exception {
        modelFactory = new ModelFactoryImpl();
        rendererFactory = new RendererFactoryImpl();
    }

    private SourceDef chew(final URL input) throws Exception {
        assertNotNull(input);

        SourceDef model = modelFactory.create(input);
        assertNotNull(model);

        Set renderers = rendererFactory.create(model);
        assertNotNull(renderers);

        Iterator iter = renderers.iterator();
        assertNotNull(iter);
        assertTrue(iter.hasNext());

        while (iter.hasNext()) {
            Renderer renderer = (Renderer)iter.next();
            assertNotNull(renderer);

            StringWriter writer = new StringWriter();
            renderer.render(writer);

            System.err.println(writer);
        }

        return model;
    }

    private SourceDef chew(final String resource) throws Exception {
        return chew(getClass().getResource(resource));
    }

    public void testSimple() throws Exception {
        chew("SimpleTest.groovy");
    }

    public void testSimpleInterface() throws Exception {
        chew("SimpleInterface.groovy");
    }

    public void testSimpleScript() throws Exception {
        SourceDef def = chew("SimpleScript.groovy");
        assertTrue(def.hasStatements());
    }
}