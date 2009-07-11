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

import org.codehaus.groovy.maven.feature.Configuration;
import org.codehaus.groovy.maven.feature.Feature;
import org.codehaus.groovy.maven.feature.FeatureException;
import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.ProviderException;
import org.codehaus.groovy.maven.feature.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides support for {@link Provider} implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class ProviderSupport
    implements Provider
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final String key;

    protected final Configuration config;

    protected Version version;

    protected Boolean supported;

    protected Map features;

    protected ProviderSupport(final String key, final Configuration config) {
        assert key != null;
        assert config != null;

        this.key = key;
        this.config = config;
    }

    protected ProviderSupport(final String key) {
        this(key, new Configuration());
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

    protected abstract Version detectVersion();

    public synchronized Version version() {
        if (version == null) {
            version = detectVersion();
        }

        return version;
    }

    protected boolean detectSupported() {
        return true;
    }

    public synchronized boolean supported() {
        if (supported == null) {
            supported = Boolean.valueOf(detectSupported());
        }

        return supported.booleanValue();
    }

    public void require() {
        if (!supported()) {
            throw new ProviderException("Provider not supported: " + key());
        }
    }

    public Configuration config() {
        return config;
    }

    protected abstract Map detectFeatures();

    public synchronized Collection features() {
        if (features == null) {
            Map detected = detectFeatures();

            log.debug("Detected features:");

            for (Iterator iter = detected.values().iterator(); iter.hasNext();) {
                Feature feature = (Feature)iter.next();

                log.debug("    {}", feature);

                // If the feature is one of ours, then register ourselves and give the feature access
                if (feature instanceof FeatureSupport) {
                    ((FeatureSupport)feature).register(this);
                }
            }
            
            features = new HashMap(detected);
        }

        return Collections.unmodifiableCollection(features.values());
    }

    public synchronized Feature feature(final String key) {
        assert key != null;

        // HACK: Just make the damn thing init we can get to the map
        if (features == null) {
            features();
        }

        if (!features.containsKey(key)) {
            throw new FeatureException("Unknown feature: " + key);
        }
        
        return (Feature)features.get(key);
    }

    public Feature feature(final Class key) {
        assert key != null;

        return feature(key.getName());
    }

    //
    // Common Helpers
    //

    public static String asString(final Provider provider) {
        assert provider != null;

        StringBuffer buff = new StringBuffer();

        buff.append("[");
        buff.append(provider.key());
        buff.append("]");

        // noinspection StringEquality
        if (provider.key() != provider.name()) {
            buff.append(" '");
            buff.append(provider.name());
            buff.append("'");
        }

        buff.append(" (version: ");
        buff.append(provider.version());
        buff.append(", type: ");
        buff.append(provider.getClass().getName());
        buff.append(")");

        return buff.toString();
    }
}