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

package org.codehaus.groovy.maven.plugin.execute;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Provides property resolution access to Groovy executions.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class GroovyMavenProjectAdapter
    extends MavenProjectDelegateAdapter
{
    private final MavenSession session;

    private final Map properties;

    private final Map defaults;
    
    private Properties props;

    public GroovyMavenProjectAdapter(final MavenProject project, final MavenSession session, final Map properties, final Map defaults) {
        super(project);

        this.session = session;
        this.properties = properties;
        this.defaults = defaults;
    }

    public synchronized Properties getProperties() {
        // Lazily construct a custom properties class to handle resolving properties as we want them
        if (props == null) {
            props = new EvaluatingProperties();
        }

        return props;
    }

    /**
     * Custom properties handling to resolve for Groovy executions.
     */
    private class EvaluatingProperties
        extends Properties
    {
        private final Logger log = LoggerFactory.getLogger(getClass());

        private final ExpressionEvaluator evaluator = new ExpressionEvaluatorImpl(session, GroovyMavenProjectAdapter.this);

        public EvaluatingProperties() {
            // Populate the base properties from the original model properties (so iter-based operations work as expected)
            putAll(getModel().getProperties());

            // Add custom execution properties
            if (properties != null) {
                putAll(properties);
            }

            if (log.isDebugEnabled() && props != null && !props.isEmpty()) {
                log.debug("Properties: ");

                List keys = new ArrayList();
                keys.addAll(props.keySet());

                Collections.sort(keys);

                for (Iterator iter = keys.iterator(); iter.hasNext();) {
                    String name = (String)iter.next();
                    String value = props.getProperty(name);

                    log.debug("    {} -> {}", name, value);
                }
            }
        }

        //
        // NOTE: lookup() and get() are marked as public intentionally... as they could be potentially useful by some
        //       advanced scripts which need richer access to properties.  Though, I think Groovy's reflector muck
        //       can actually invoke the privates just as well.
        //

        public Object lookup(final Object key) {
            // First try ourself (pom + custom)
            Object value = super.get(key);

            // Then try execution (system) properties
            if (value == null) {
                value = session.getExecutionProperties().get(key);
            }

            // Then try defaults (from adapter, not from properties, which is not used)
            if (value == null && GroovyMavenProjectAdapter.this.defaults != null) {
                value = GroovyMavenProjectAdapter.this.defaults.get(key);
            }

            return value;
        }

        public Object get(final Object key, final boolean resolve) {
            Object value = lookup(key);

            // If the value is a string, evaluate it to get expressions to expand
            if (resolve && value instanceof String) {
                try {
                    value = evaluator.evaluate((String)value);
                }
                catch (ExpressionEvaluationException e) {
                    // If something bad happens just puke it up
                    throw new RuntimeException(e);
                }
            }

            log.trace("Getting value: {} = {}", key, value);

            return value;
        }

        public Object get(final Object key) {
            return get(key, true);
        }

        public String getProperty(final String name) {
            // We have to override getProperty() as the default impl gets the value from super.get() instead of get()
            Object value = get(name);

            log.trace("Getting property: {} = {}", name, value);

            return value != null ? String.valueOf(value) : null;
        }

        public Object put(final Object key, final Object value) {
            log.trace("Putting value: {} = {}", key, value);

            // Have to set in the original to preserve between executions
            getDelegate().getProperties().put(key, value);

            // But need to update ourself so resolution in the same execution works too
            return super.put(key, value);
        }
    }
}
