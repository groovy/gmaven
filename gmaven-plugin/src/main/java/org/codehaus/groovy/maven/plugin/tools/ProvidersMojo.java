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

package org.codehaus.groovy.maven.plugin.tools;

import org.codehaus.groovy.maven.feature.Feature;
import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.ProviderManager;
import org.codehaus.groovy.maven.feature.ProviderRegistry;
import org.codehaus.groovy.maven.feature.ProviderSelector;
import org.codehaus.groovy.maven.plugin.ProviderMojoSupport;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Displays information about the Groovy runtime providers which are configured and selected.
 *
 * @goal providers
 * @requiresProject false
 * @since 1.0-beta-3
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @version $Id$
 */
public class ProvidersMojo
    extends ProviderMojoSupport
{
    protected void doExecute() throws Exception {
        if (log.isTraceEnabled()) {
            logEnvironment();
        }

        ProviderManager manager = getProviderManager();
        log.debug("Provider manager: {}", manager);

        ProviderRegistry registry = manager.getRegistry();
        log.debug("Provider registry: {}", registry);

        ProviderSelector selector = manager.getSelector();
        log.debug("Provider selector: {}", selector);

        // Before we can get the list of registered providers, we need to select one first,
        // so pick the default and save any failure for later inspection

        Provider selected = null;
        Throwable selectFailure = null;
        try {
            selected = provider();
            log.debug("Selected: {}", selected);
        }
        catch (Throwable t) {
            log.debug("Selection failure: " + t, t);
            selectFailure = t;
        }

        log.info("");

        log.info("Provider selection: {}", getProviderSelection());
        log.info("");
        
        Map providers = registry.providers();
        if (providers == null || providers.isEmpty()) {
            log.info("No providers registered");
        }
        else {
            log.info("Found {} registered providers:", String.valueOf(providers.size()));

            for (Iterator iter = providers.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                Provider provider = (Provider) providers.get(key);

                logProvider(provider, "    ");

                log.info("");
            }

            if (selected != null) {
                log.info("Selected provider:");
                log.info("    {}", selected);
            }
            else if (selectFailure != null) {
                log.info("No provider was selected; Failures occured while selecting: {}", selectFailure.toString());
            }
            else {
                log.info("No provider was selected and no failure was detected; The gods must be crazy...");
            }
        }

        log.info("");
    }

    protected void logEnvironment() {
        log.trace("ClassLoader '{}' Class-Path:", getClass().getClassLoader());

        URL[] urls = ((URLClassLoader)getClass().getClassLoader()).getURLs();
        
        for (int i=0; i<urls.length; i++) {
            log.trace("    {}", urls[i]);
        }

        log.debug("Plugin Artifacts:");

        for (Iterator iter = pluginArtifactMap.keySet().iterator(); iter.hasNext();) {
            log.trace("    {}", iter.next());
        }
    }

    private void logProvider(final Provider provider, final String pad) {
        assert provider != null;
        assert pad != null;

        log.info("{}{}", pad, provider);

        logFeatures(provider, pad + "    ");
    }

    private void logFeatures(final Provider provider, final String pad) {
        assert provider != null;
        assert pad != null;

        Collection features = provider.features();

        if (features.isEmpty()) {
            log.info("{}No features", pad);
        }
        else {
            log.info("{}Features:", pad);

            for (Iterator iter = features.iterator(); iter.hasNext();) {
                Feature feature = (Feature) iter.next();
                log.info("{}    {}", pad, feature.key());
            }
        }
    }
}