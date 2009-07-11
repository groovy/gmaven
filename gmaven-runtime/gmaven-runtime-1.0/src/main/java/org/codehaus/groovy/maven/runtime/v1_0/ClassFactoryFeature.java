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

package org.codehaus.groovy.maven.runtime.v1_0;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyResourceLoader;
import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.feature.ComponentException;
import org.codehaus.groovy.maven.feature.support.ComponentSupport;
import org.codehaus.groovy.maven.feature.support.FeatureSupport;
import org.codehaus.groovy.maven.runtime.ClassFactory;
import org.codehaus.groovy.maven.runtime.support.util.ResourceLoaderImpl;
import org.codehaus.groovy.maven.runtime.util.ClassSource;
import org.codehaus.groovy.maven.runtime.util.ResourceLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Provides the class factory feature.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ClassFactoryFeature
    extends FeatureSupport
{
    public ClassFactoryFeature() {
        super(ClassFactory.KEY);
    }

    protected Component doCreate() throws Exception {
        return new ClassFactoryImpl();
    }

    //
    // ClassFactoryImpl
    //

    private class ClassFactoryImpl
        extends ComponentSupport
        implements ClassFactory
    {
        private ClassFactoryImpl() {
            super(ClassFactoryFeature.this);
        }

        public Class create(final ClassSource classSource, final ClassLoader classLoader, final ResourceLoader resourceLoader) throws Exception {
            assert classSource != null;
            assert classLoader != null;
            // resourceLoader can be null

            GroovyClassLoader groovyClassLoader = createGroovyClassLoader(classLoader, resourceLoader);

            GroovyCodeSource codeSource = createGroovyCodeSource(classSource);

            return groovyClassLoader.parseClass(codeSource);
        }

        public Class create(final ClassSource classSource, final ClassLoader classLoader) throws Exception {
            return create(classSource, classLoader, null);
        }

        public Class create(final String className, final ClassLoader classLoader, ResourceLoader resourceLoader) throws Exception {
            assert className != null;
            assert classLoader != null;
            // resourceLoader can be null

            if (resourceLoader == null) {
                resourceLoader = new ResourceLoaderImpl(classLoader);
            }

            URL source = resourceLoader.loadResource(className);

            if (source == null) {
                throw new ComponentException("Missing source for: " + className);
            }

            return create(new ClassSource(source), classLoader, resourceLoader);
        }

        public Class create(final String className, final ClassLoader classLoader) throws Exception {
            return create(className, classLoader, null);
        }

        private GroovyClassLoader createGroovyClassLoader(final ClassLoader classLoader, final ResourceLoader resourceLoader) {
            assert classLoader != null;
            // resourceLoader can be null

            GroovyClassLoader groovyClassLoader = new GroovyClassLoader(classLoader);

            groovyClassLoader.setResourceLoader(createGroovyResourceLoader(classLoader, resourceLoader));

            return groovyClassLoader;
        }

        private GroovyResourceLoader createGroovyResourceLoader(final ClassLoader classLoader, ResourceLoader resourceLoader) {
            assert classLoader != null;
            // resourceLoader can be null

            if (resourceLoader == null) {
                resourceLoader = new ResourceLoaderImpl(classLoader);
            }

            return new GroovyResourceLoaderAdapter(resourceLoader);
        }

        private GroovyCodeSource createGroovyCodeSource(final ClassSource source) throws IOException {
            assert source != null;

            int count = 0;

            if (source.url != null) {
                count++;
            }

            if (source.file != null) {
                count++;
            }

            if (source.body != null) {
                count++;
            }

            if (count == 0) {
                throw new ComponentException("Invalid class source; must define a URL, File or Body: " + source);
            }

            if (count != 1) {
                throw new ComponentException("Invalid class source; only one of URL, File or Body is allowed: " + source);
            }

            if (source.url != null) {
                return new GroovyCodeSource(source.url);
            }

            if (source.file != null) {
                return new GroovyCodeSource(source.file);
            }

            if (source.body != null) {
                return new GroovyCodeSource(source.body.input, source.body.name, source.body.codeBase);
            }

            throw new InternalError();
        }
    }

    //
    // GroovyResourceLoaderAdapter
    //

    private class GroovyResourceLoaderAdapter
        implements GroovyResourceLoader
    {
        private final ResourceLoader loader;

        public GroovyResourceLoaderAdapter(final ResourceLoader loader) {
            assert loader != null;

            this.loader = loader;
        }

        public URL loadGroovySource(final String name) throws MalformedURLException {
            return loader.loadResource(name);
        }
    }
}