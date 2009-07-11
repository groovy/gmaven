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

import org.codehaus.groovy.maven.gossip.config.ConfigurationException;
import org.codehaus.groovy.maven.gossip.config.MissingPropertyException;
import org.codehaus.groovy.maven.gossip.model.Configuration;
import org.codehaus.groovy.maven.gossip.model.Source;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * System-property configuration source.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SystemPropertySource
    extends Source
{
    public SystemPropertySource() {}

    public SystemPropertySource(final String name) {
        setName(name);
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Configuration load() throws Exception {
        if (name == null) {
            throw new MissingPropertyException("name");
        }

        String value = System.getProperty(name);

        if (value == null) {
            log.trace("Unable to load; property not set: {}", name);
            return null;
        }

        log.debug("Converting to URL: {}", value);
        
        URL url = null;

        try {
            url = new URL(value);
        }
        catch (MalformedURLException e) {
            File file = new File(value);

            if (file.exists()) {
                url = file.toURI().toURL();
            }
        }

        if (url == null) {
            throw new ConfigurationException("Unable to load; unable to resolve target: " + value);
        }

        return load(url);
    }

    public String toString() {
        return "SystemPropertySource{" +
                "name='" + name + '\'' +
                '}';
    }
}