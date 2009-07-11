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

package org.codehaus.groovy.maven.runtime.support.util;

import org.codehaus.groovy.maven.runtime.support.stubgen.parser.SourceType;
import org.codehaus.groovy.maven.runtime.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Basic {@link ResourceLoader} implemenation.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ResourceLoaderImpl
    implements ResourceLoader
{
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    protected ClassLoader classLoader;

    public ResourceLoaderImpl(final ClassLoader classLoader) {
        assert classLoader != null;

        this.classLoader = classLoader;
    }

    public URL loadResource(final String name) throws MalformedURLException {
        return resolve(name, classLoader);
    }

    protected String toResourceName(final String className) {
        assert className != null;
        
        // Figure out what resource to load
        String resource = className;

        if (!resource.startsWith("/")) {
            resource = "/" + resource;
        }

        if (!resource.endsWith(SourceType.GROOVY_EXT)) {
            resource = resource.replace('.', '/');
            resource += SourceType.GROOVY_EXT;
        }

        return resource;
    }

    protected URL resolve(final String className, final ClassLoader classLoader) throws MalformedURLException {
        assert className != null;
        assert classLoader != null;

        // log.debug("Resolve; class name: {}", className);

        String resource = toResourceName(className);

        // log.debug("Resolve; resource name {}", resource);

        URL url = classLoader.getResource(resource);

        // log.debug("From CL: {}", url);

        if (url == null) {
            // Not sure if this is nessicary or not...
            url = Thread.currentThread().getContextClassLoader().getResource(resource);

            // log.debug("From TCL: {}", url);
        }

        return url;
    }
}