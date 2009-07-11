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
import org.codehaus.groovy.maven.feature.FeatureException;
import org.codehaus.groovy.maven.feature.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides support for {@link Feature} implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class FeatureSupport
    implements Feature
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final String key;

    protected final Configuration config;

    protected final boolean supported;

    protected Provider provider;

    protected FeatureSupport(final String key, final boolean supported) {
        assert key != null;
        
        this.key = key;
        this.supported = supported;
        this.config = new Configuration();
    }

    protected FeatureSupport(final String key) {
        this(key, true);
    }

    public String toString() {
        return asString(this);
    }

    public int hashCode() {
        return key().hashCode();
    }

    public String key() {
        return key;
    }

    public String name() {
        return key();
    }

    public boolean supported() {
        return supported;
    }

    public void require() {
        if (!supported()) {
            throw new FeatureException("Feature not supported: " + key());
        }
    }

    public Configuration config() {
        return config;
    }

    public Component create(final Configuration context) throws Exception {
        assert context != null;

        Component component = create();

        // Merge in the context
        Configuration c = component.config();
        c.merge(context);

        return component;
    }

    public Component create() throws Exception {
        // First we need to be supported, so require it
        require();

        // Then install the provider CL into the TCL to get better behaved CL mucko
        final ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(provider().getClass().getClassLoader());

        try {
            Component component = doCreate();

            // Merge our configuraiton
            Configuration c = component.config();
            c.merge(config());

            return component;
        }
        finally {
            // Reset back to the previous TCL
            Thread.currentThread().setContextClassLoader(tcl);
        }
    }

    protected abstract Component doCreate() throws Exception;

    //
    // Provider registration hooks to work with ProviderSupport
    //

    /* package */ synchronized void register(final Provider provider) {
        if (this.provider != null) {
            throw new IllegalStateException(
                    "Duplicate provider registration with feature: " + this +
                    ", previous provider: " + this.provider +
                    ", current provider: " + provider);
        }

        this.provider = provider;
    }

    protected synchronized Provider provider() {
        if (provider == null) {
            throw new IllegalStateException("Provider has not been registered with feature: " + this);
        }

        return provider;
    }

    public static String asString(final Feature feature) {
        assert feature != null;

        StringBuffer buff = new StringBuffer();

        buff.append("[");
        buff.append(feature.key());
        buff.append("]");

        // noinspection StringEquality
        if (feature.key() != feature.name()) {
            buff.append(" '");
            buff.append(feature.name());
            buff.append("'");
        }

        buff.append(" (supported: ");
        buff.append(feature.supported());
        buff.append(", type: ");
        buff.append(feature.getClass().getName());
        buff.append(")");

        return buff.toString();
    }
}