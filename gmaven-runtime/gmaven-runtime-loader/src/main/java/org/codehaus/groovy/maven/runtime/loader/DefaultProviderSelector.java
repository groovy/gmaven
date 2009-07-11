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
import org.codehaus.groovy.maven.feature.ProviderLoader;
import org.codehaus.groovy.maven.feature.ProviderRegistry;
import org.codehaus.groovy.maven.feature.ProviderSelector;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Default {@link ProviderSelector}.
 *
 * @plexus.component role="org.codehaus.groovy.maven.feature.ProviderSelector"
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DefaultProviderSelector
    implements ProviderSelector, Contextualizable
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private PlexusContainer container;

    public void contextualize(final Context context) throws ContextException {
        assert context != null;

        container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }

    protected PlexusContainer getContainer() {
        if (container == null) {
            throw new IllegalStateException("Container not bound");
        }

        return container;
    }

    public Provider select(final ProviderRegistry registry, final String selection) throws Exception {
        assert registry != null;
        assert selection != null;

        log.trace("Select: {}", selection);

        register(registry, selection);

        Provider provider = null;

        if (SELECT_ANY.equals(selection)) {
            provider = selectAny(registry);
        }
        else {
            String[] keys = selection.split(",");

            for (int i=0; i<keys.length; i++) {
                Provider tmp = registry.lookup(keys[i]);
                
                if (tmp != null) {
                    provider = tmp;
                    break;
                }
            }
        }

        if (log.isTraceEnabled()) {
            if (provider == null) {
                log.trace("No matching providers found for selection: {}", selection);
            }
            else if (!provider.supported()) {
                log.trace("Found unsupported provider matching selection: {}, found: {}", selection, provider);
            }
        }
        
        return provider;
    }

    private Provider selectAny(final ProviderRegistry registry) {
        assert registry != null;

        Map supported = registry.providers(true);

        Provider provider = null;

        if (supported != null && !supported.isEmpty()) {
            provider = (Provider) supported.values().iterator().next();
        }

        return provider;
    }

    private void register(final ProviderRegistry registry, final String selection) throws Exception {
        assert registry != null;
        assert selection != null;

        // First run discovery
        Map discovered = discover(registry, selection);

        // If we didn't find anything then puke
        if (discovered == null || discovered.isEmpty()) {
            log.debug("No providers discovered for selection: {}", selection);
        }
        else {
            log.debug("Registering {} providers:", String.valueOf(discovered.size()));

            for (Iterator iter = discovered.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                Provider provider = (Provider) discovered.get(key);

                log.debug("    {} -> {}", key, provider);

                // Complain if we found a mismatch of keys
                if (!key.equals(provider.key())) {
                    log.warn("Found mismatch of provider key; discovered key: {}, provider's key: {}", key, provider.key());
                }

                Provider replaced = registry.register(provider);

                // Complain if we replaced a provider
                if (replaced != null) {
                    log.warn("Replaced provider; key: {}, current: {}, replaced: {}", new Object[] { key, provider, replaced });
                }
            }
        }
    }

    private Map discover(final ProviderRegistry registry, final String selection) throws Exception {
        assert registry != null;
        assert selection != null;

        log.debug("Discovering providers for selection: {}", selection);

        Map discovered = null;

        String[] keys = selection.split(",");

        // Attempt to discover providers for each key
        for (int i=0; i<keys.length; i++) {
            // Don't attempt to discover psuedo providers
            if (keys[i].equals(SELECT_ANY)) {
                continue;
            }

            try {
                // First see if the registry already has a provider for this key
                if (registry.lookup(keys[i]) != null) {
                    log.debug("Provider already registered for: {}", keys[i]);

                    // Skip loading and re-use the registered provider
                    continue;
                }

                Map found = load(keys[i]);

                if (found != null && !found.isEmpty()) {
                    // Late init the discovered providers map
                    if (discovered == null) {
                        discovered = new HashMap();
                    }

                    discovered.putAll(found);
                }
            }
            catch (Exception e) {
                log.debug("Failed to load providers for key: {}", keys[i], e);
            }
        }

        return discovered;
    }

    protected Map load(final String key) throws Exception {
        assert key != null;

        Map found = null;

        Map loaders = findLoaders();

        if (loaders == null || loaders.isEmpty()) {
            log.debug("No provider loaders were found");
        }
        else {
            log.debug("Looking for provider {} in {}", key, loaders);

            for (Iterator iter = loaders.values().iterator(); iter.hasNext();) {
                ProviderLoader loader = (ProviderLoader) iter.next();

                log.debug("Trying to load {} from {}", key, loader);

                try {
                    Map loaded = loader.load(key);

                    if (loaded != null && !loaded.isEmpty()) {
                        found = loaded;
                        break;
                    }
                }
                catch (Exception e) {
                    log.warn("Failed to load provider from: {}", loader, e);
                }
            }
        }

        return found;
    }

    /**
     * Find any provider loaders which are available in the container.
     */
    private Map findLoaders() {
        Map loaders = getContainer().getComponentDescriptorMap(ProviderLoader.class.getName());
        if (loaders == null) {
            throw new Error("No provider loaders found");
        }
        
        Set keys = loaders.keySet();

        Map found = null;

        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            String key = (String)iter.next();

            ProviderLoader loader;
            
            try {
                loader = (ProviderLoader) getContainer().lookup(ProviderLoader.class.getName(), key);
            }
            catch (Exception e) {
                log.warn("Failed to lookup provider loader for key: {}", key, e);
                continue;
            }

            if (loader != null) {
                if (found == null) {
                    found = new HashMap();
                }

                found.put(key, loader);
            }
        }

        return found;
    }
}