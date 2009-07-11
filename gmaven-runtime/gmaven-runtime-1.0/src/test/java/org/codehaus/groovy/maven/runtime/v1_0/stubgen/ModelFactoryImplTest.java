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

package org.codehaus.groovy.maven.runtime.v1_0.stubgen;

import junit.framework.TestCase;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ModelFactory;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.SourceDef;

import java.net.URL;

/**
 * Tests for the {@link ModelFactoryImpl} class.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ModelFactoryImplTest
    extends TestCase
{
    private ModelFactory factory;

    protected void setUp() throws Exception {
        factory = new ModelFactoryImpl();
    }

    public void testSimple() throws Exception {
        URL input = getClass().getResource("SimpleTest.groovy");
        assertNotNull(input);

        System.out.println(input);

        SourceDef model = factory.create(input);
        assertNotNull(model);

        System.out.println(model);

        //
        // TODO: Verify
        //
    }
}