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

package org.codehaus.groovy.maven.runtime.loader;

import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.ProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Default {@link ProviderRegistry}.
 *
 * @plexus.component role="org.codehaus.groovy.maven.feature.ProviderRegistry"
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DefaultProviderRegistry
    implements ProviderRegistry
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final Map providers = new HashMap();

    public Map providers() {
        return providers;
    }

    public Map providers(final boolean supported) {
        Map selected = new HashMap();

        for (Iterator iter = providers().values().iterator(); iter.hasNext();) {
            Provider provider = (Provider)iter.next();

            if (supported == provider.supported()) {
                selected.put(provider.key(), provider);
            }
        }

        return selected;
    }

    public Provider lookup(final String key) {
        assert key != null;

        log.trace("Lookup: {}", key);

        return (Provider) providers().get(key);
    }

    public Provider register(final Provider provider) {
        assert provider != null;

        return register(provider.key(), provider);
    }

    private Provider register(final String key, final Provider provider) {
        assert key != null;
        assert provider != null;

        log.trace("Register: {} -> {}", key, provider);

        return (Provider) providers().put(key, provider);
    }

    public Provider deregister(final String key) {
        assert key != null;

        log.trace("Deregister: {}", key);

        return (Provider) providers().remove(key);
    }
}