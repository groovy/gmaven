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

package org.codehaus.groovy.maven.gossip.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Configuration node.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Configuration
    extends RootNode
{
    /** @noinspection UnusedDeclaration */
    private final String version = "1.0";

    private Properties properties;

    private List sources;

    private List profiles;

    public Configuration() {}

    public String getVersion() {
        return version;
    }

    public Properties properties() {
        if (properties == null) {
            properties = new Properties();
        }

        return properties;
    }

    public List sources() {
        if (sources == null) {
            sources = new ArrayList();
        }

        return sources;
    }

    public void addSource(final Source node) {
        assert node != null;

        sources().add(node);
        node.setParent(this);
    }

    public List profiles() {
        if (profiles == null) {
            profiles = new ArrayList();
        }

        return profiles;
    }

    public void addProfile(final Profile node) {
        assert node != null;

        profiles().add(node);
        node.setParent(this);
    }

    //
    // Merging
    //
    
    public void merge(final Configuration node) {
        assert node != null;

        log.trace("Merging with: {}", node);
        
        if (node.properties != null && !node.properties.isEmpty()) {
            mergeProperties(node.properties, properties());
        }

        if (node.sources != null && !node.sources.isEmpty()) {
            mergeSources(node.sources, sources());
        }

        if (node.profiles != null && !node.profiles.isEmpty()) {
            mergeProfiles(node.profiles, profiles());
        }

    }

    private void mergeSources(final List source, final List target) {
        assert source != null;
        assert target != null;

        log.trace("Merging sources");

        target.addAll(source);
    }

    private void mergeProfiles(final List source, final List target) {
        assert source != null;
        assert target != null;

        log.trace("Merging profiles");

        target.addAll(source);
    }
}