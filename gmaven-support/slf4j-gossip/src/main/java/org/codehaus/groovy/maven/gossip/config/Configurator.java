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

package org.codehaus.groovy.maven.gossip.config;

import java.net.URL;
import java.util.Iterator;

import org.codehaus.groovy.maven.gossip.InternalLogger;
import org.codehaus.groovy.maven.gossip.model.Configuration;
import org.codehaus.groovy.maven.gossip.model.EffectiveProfile;
import org.codehaus.groovy.maven.gossip.model.Profile;
import org.codehaus.groovy.maven.gossip.model.Source;
import org.codehaus.groovy.maven.gossip.model.trigger.AlwaysTrigger;
import org.codehaus.groovy.maven.gossip.model.source.URLSource;
import org.codehaus.groovy.maven.gossip.model.filter.ConsoleWriter;

/**
 * ???
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Configurator
{
    private static final String BOOTSTRAP_RESOURCE = "/META-INF/org/codehaus/groovy/maven/gossip/bootstrap.properties";

    private final InternalLogger log = InternalLogger.getLogger(getClass());

    public EffectiveProfile configure() {
        Configuration root = new Configuration();

        EffectiveProfile profile = new EffectiveProfile();

        try {
            // Load the bootstrap configuration
            Configuration bootstrap = loadBootstrap();

            // Resolve sources and merge
            Configuration config = resolve(bootstrap, root);

            // Configure the active profiles
            configureActiveProfiles(profile, config);
        }
        catch (Throwable t) {
            log.error("Failed to configure; using fall-back provider", t);
        }

        if (profile.profiles().isEmpty()) {
            log.debug("No profiles were activated; using fall-back");

            Profile p = createFallbackProfile();
            profile.addProfile(p);
        }

        return profile;
    }

    private void configureActiveProfiles(final EffectiveProfile profile, final Configuration config) throws Exception {
        assert profile != null;
        assert config != null;

        log.debug("Configure active profiles: {}", config);

        for (Iterator iter = config.profiles().iterator(); iter.hasNext();) {
            Profile p = (Profile) iter.next();

            if (p.isActive()) {
                log.debug("    {}", p);
                profile.addProfile(p);
            }
        }
    }

    private Profile createFallbackProfile() {
        Profile p = new Profile("fallback");

        p.addTrigger(new AlwaysTrigger());

        p.addFilter(new ConsoleWriter(ConsoleWriter.SYSOUT));

        return p;
    }

    private Configuration loadBootstrap() throws Exception {
        URL url = getClass().getClassLoader().getResource(BOOTSTRAP_RESOURCE);

        URLSource source = new URLSource(url);

        return source.load();
    }

    private Configuration resolve(final Configuration config, final Configuration base) throws Exception {
        assert config != null;
        assert base != null;
        
        for (Iterator iter=config.sources().iterator(); iter.hasNext();) {
            Source source = (Source) iter.next();

            Configuration loaded = null;

            try {
                loaded = source.load();
            }
            catch (Exception e) {
                log.error("Failed to load configuration from source: {}", source, e);
            }

            if (loaded != null) {
                // Resolve any referenced sources
                resolve(loaded, base);

                // Merge the configuration
                base.merge(loaded);
            }
        }

        return base;
    }
}