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

import org.apache.commons.lang.time.StopWatch;
import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.ProviderException;
import org.codehaus.groovy.maven.feature.ProviderManager;
import org.codehaus.groovy.maven.feature.ProviderRegistry;
import org.codehaus.groovy.maven.feature.ProviderSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Default {@link ProviderManager}.
 *
 * @plexus.component role="org.codehaus.groovy.maven.feature.ProviderManager"
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DefaultProviderManager
    implements ProviderManager
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map cachedSelection = new HashMap();

    /**
     * @plexus.requirement
     *
     * @noinspection UnusedDeclaration
     */
    private ProviderRegistry registry;

    /**
     * @plexus.requirement
     *
     * @noinspection UnusedDeclaration
     */
    private ProviderSelector selector;

    public ProviderRegistry getRegistry() {
        if (registry == null) {
            throw new IllegalStateException("Registry not bound");
        }

        return registry;
    }

    public ProviderSelector getSelector() {
        if (registry == null) {
            throw new IllegalStateException("Selector not bound");
        }

        return selector;
    }

    //
    // TODO: Expose more default selection API
    //

    private String getDefaultSelection() {
        return ProviderSelector.SELECT_DEFAULT;
    }

    //
    // TODO: Add loader and provider dynamic look up?
    //

    public Provider select(final String selection) {
        assert selection != null;

        Provider provider = (Provider) cachedSelection.get(selection);
        if (provider != null) {
            log.debug("Using cached provider '{}' for selection: {}", provider, selection);    
        }
        else {
            log.debug("Selecting provider; selection: {}", selection);

            StopWatch watch = new StopWatch();
            watch.start();

            try {
                provider = getSelector().select(getRegistry(), selection);
            }
            catch (Exception e) {
                throw new ProviderException("Selection of provider failed; selection: " + selection, e);
            }

            if (provider == null) {
                throw new ProviderException("No providers found matching selection: " + selection);
            }

            cachedSelection.put(selection, provider);
            
            watch.stop();

            log.debug("Selected provider: {} ({})", provider, watch);
        }

        return provider;
    }

    public Provider select() {
        return select(getDefaultSelection());
    }
}