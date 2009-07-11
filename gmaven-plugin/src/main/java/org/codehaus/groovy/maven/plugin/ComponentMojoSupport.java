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

package org.codehaus.groovy.maven.plugin;

import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.feature.Configuration;
import org.codehaus.groovy.maven.feature.Feature;

/**
 * Support for Mojo implementations which delegate to a feature component.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class ComponentMojoSupport
    extends ProviderMojoSupport
{
    private final String key;

    protected ComponentMojoSupport(final String key) {
        this.key = key;
    }

    private Feature cachedFeature;

    protected synchronized Feature feature() throws Exception {
        if (cachedFeature == null) {
            cachedFeature = feature(key);
        }

        return cachedFeature;
    }

    protected synchronized Feature feature(final String key) throws Exception {
        return provider().feature(key);
    }

    protected void doExecute() throws Exception {
        Feature feature = feature();

        Configuration context = new Configuration();
        configure(context);

        Component component = feature.create(context);

        process(component);
    }

    protected void configure(final Configuration context) throws Exception {
        // Nothing by default
    }

    protected abstract void process(Component component) throws Exception;

}