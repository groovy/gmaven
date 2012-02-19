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

package org.codehaus.gmaven.plugin.execute;

import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

/**
 * Container for <tt>groovy:execute</tt> source configuration.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Source
{
    public final PlexusConfiguration configuration;

    public final ExpressionEvaluator evaluator;

    public Source(final PlexusConfiguration configuration, final ExpressionEvaluator evaluator) {
        assert configuration != null;
        assert evaluator != null;

        this.configuration = configuration;
        this.evaluator = evaluator;
    }

    public String toString() {
        try {
            return escapeAsNeeded(configuration.getValue());
        } catch (PlexusConfigurationException e) {
            return configuration.toString();
        }
    }

    protected String escapeAsNeeded(String string) {
        StringBuilder sb = new StringBuilder(string);
        int index = 0;
        while (index > -1) {
            int i = sb.indexOf("\\", index);
            if (i > -1) {
                String str = sb.substring(i, i + 2);
                if (!str.contains("\\\\")) {
                    sb.insert(i, "\\");
                } else {
                    sb.delete(i, i + 1);
                }
                index = i + 3;
            } else {
                index = i;
            }
        }

        return sb.toString();
    }
}