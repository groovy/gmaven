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
 * Profile node.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Profile
    extends ContainerNode
{
    private String name;

    private Properties properties;
    
    private Activation activation;

    private List loggers;

    private FilterChain filters;

    public Profile() {}

    public Profile(final String name) {
        assert name != null;

        setName(name);
    }

    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Properties properties() {
        if (properties == null) {
            properties = new Properties();
        }

        return properties;
    }
    
    public Activation activation() {
        if (activation == null) {
            activation = new Activation();
            activation.setParent(this);
        }

        return activation;
    }

    public List triggers() {
        return activation().triggers();
    }

    public void addTrigger(final Trigger node) {
        activation().addTrigger(node);
    }

    public List loggers() {
        if (loggers == null) {
            loggers = new ArrayList();
        }

        return loggers;
    }

    public void addLogger(final Logger node) {
        assert node != null;

        loggers().add(node);
        node.setParent(this);
    }

    public FilterChain filters() {
        if (filters == null) {
            filters = new FilterChain();
        }

        return filters;
    }

    public void addFilter(final Filter node) {
        filters().addFilter(node);
    }

    public boolean isActive() {
        return activation().isActive();
    }

    //
    // Merging
    //

    public void merge(final Profile node) {
        assert node != null;

        log.debug("Merging with: {}", node);
        
        if (node.properties != null && !node.properties.isEmpty()) {
            mergeProperties(node.properties, properties());
        }

        // TODO: activation

        if (node.loggers != null && !node.loggers.isEmpty()) {
            mergeLoggers(node.loggers, loggers());
        }

        // TODO: filters
    }

    private void mergeLoggers(final List source, final List target) {
        assert source != null;
        assert target != null;

        log.debug("Merging loggers");

        target.addAll(source);
    }
}