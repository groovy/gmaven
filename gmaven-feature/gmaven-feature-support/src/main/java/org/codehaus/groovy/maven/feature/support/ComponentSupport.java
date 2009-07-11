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

package org.codehaus.groovy.maven.feature.support;

import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.feature.Configuration;
import org.codehaus.groovy.maven.feature.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides support for {@link Component} implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ComponentSupport
    implements Component
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Feature feature;

    protected final Configuration config;

    protected ComponentSupport(final Feature feature, final Configuration config) {
        assert feature != null;
        assert config != null;

        this.feature = feature;
        this.config = config;
    }

    protected ComponentSupport(final Feature feature) {
        this(feature, new Configuration());
    }

    public Feature feature() {
        return feature;
    }

    public Configuration config() {
        return config;
    }
}