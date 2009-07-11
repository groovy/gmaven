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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.ProviderLoader;

/**
 * Default {@link ProviderLoader}.
 *
 * @plexus.component role="org.codehaus.groovy.maven.feature.ProviderLoader" role-hint="default" instantiation-strategy="singleton"
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DefaultProviderLoader
    extends LoaderSupport
    implements ProviderLoader
{
    public Map load(final String key) throws Exception {
        assert key != null;

        Map found = null;

        Map providers = findProviders();

        if (providers == null || providers.isEmpty()) {
            log.debug("No providers were found");
        }
        else {
            log.debug("Looking for provider {} in {}", key, providers);

            Provider provider = (Provider) providers.get(key);

            if (provider != null) {
                found = Collections.singletonMap(key, provider);
            }
        }

        return found;
    }

    /**
     * Find any providers which are available in the container.
     */
    private Map findProviders() {
        Set keys = getContainer().getComponentDescriptorMap(Provider.class.getName()).keySet();

        Map found = null;

        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            String key = (String)iter.next();

            Provider provider;
            
            try {
                provider = (Provider) getContainer().lookup(Provider.class.getName(), key);
            }
            catch (Exception e) {
                log.warn("Failed to lookup provider for key: {}", key, e);
                continue;
            }

            if (provider != null) {
                if (found == null) {
                    found = new HashMap();
                }

                found.put(key, provider);
            }
        }

        return found;
    }
}