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

import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

/**
 * Plexus converter for {@link Source} objects.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SourceConverter
    extends AbstractConfigurationConverter
    implements LogEnabled
{
    private Logger log;

    public void enableLogging(final Logger logger) {
        assert logger != null;

        this.log = logger;
    }

    public boolean canConvert(final Class type) {
        assert type != null;

        return Source.class.isAssignableFrom(type);
    }

    public Object fromConfiguration(final ConverterLookup converterLookup,
                                    final PlexusConfiguration configuration,
                                    final Class type,
                                    final Class baseType,
                                    final ClassLoader classLoader,
                                    final ExpressionEvaluator evaluator,
                                    final ConfigurationListener listener)
            throws ComponentConfigurationException
    {
        try {
            return new Source(configuration, evaluator);
        }
        catch (Exception e) {
            throw new ComponentConfigurationException(e);
        }
    }
}