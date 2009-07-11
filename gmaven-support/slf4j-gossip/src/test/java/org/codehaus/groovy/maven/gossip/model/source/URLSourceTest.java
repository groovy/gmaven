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
import org.codehaus.groovy.maven.gossip.model.Configuration;

import java.net.URL;

/**
 * Tests for the {@link org.codehaus.groovy.maven.gossip.model.source.URLSource} class.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class URLSourceTest
    extends TestCase
{
    public void test1() throws Exception {
        URLSource s = new URLSource();

        URL url = getClass().getResource("config1.properties");
        assertNotNull(url);

        s.setUrl(url);

        Configuration c = s.load();
        assertNotNull(c);
    }
}