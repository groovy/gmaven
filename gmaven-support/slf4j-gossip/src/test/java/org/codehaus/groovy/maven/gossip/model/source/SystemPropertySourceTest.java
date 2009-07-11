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

package org.codehaus.groovy.maven.gossip.model.source;

import junit.framework.TestCase;
import org.codehaus.groovy.maven.gossip.config.ConfigurationException;
import org.codehaus.groovy.maven.gossip.model.Configuration;

/**
 * Tests for the {@link org.codehaus.groovy.maven.gossip.model.source.SystemPropertySource} class.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SystemPropertySourceTest
    extends TestCase
{
    public void test1() throws Exception {
        SystemPropertySource s = new SystemPropertySource();
        String name = "foo.bar";
        s.setName(name);

        System.setProperty(name, "no such file anywhere I hope");
        try {
            s.load();
            fail();
        }
        catch (ConfigurationException e) {
            // expected
        }
    }

    public void test2() throws Exception {
        SystemPropertySource s = new SystemPropertySource();
        String name = "foo.bar";
        s.setName(name);

        System.getProperties().remove(name);
        
        Configuration c = s.load();
        assertNull(c);
    }

    public void test3() throws Exception {
        try {
            SystemPropertySource s = new SystemPropertySource();
            s.load();
            fail();
        }
        catch (ConfigurationException expected) {}
    }
}