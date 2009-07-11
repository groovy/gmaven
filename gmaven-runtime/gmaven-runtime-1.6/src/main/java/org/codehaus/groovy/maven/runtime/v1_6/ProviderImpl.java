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

package org.codehaus.groovy.maven.runtime.v1_6;

import org.codehaus.groovy.maven.feature.Feature;
import org.codehaus.groovy.maven.feature.Version;
import org.codehaus.groovy.maven.feature.support.ProviderSupport;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides support for Groovy 1.6.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ProviderImpl
    extends ProviderSupport
{
    public static final String KEY = "1.6";

    public ProviderImpl() {
        super(KEY);
    }

    protected Map detectFeatures() {
        Feature[] features = {
            new ClassFactoryFeature(),
            new ScriptExecutorFeature(),
            new ClassCompilerFeature(),
            new StubCompilerFeature(),
            new ConsoleFeature(),
            new ShellFeature(),
            new TraceSanitizerFeature(),
        };

        Map<String,Feature> map = new LinkedHashMap<String,Feature>();

        for (Feature feature : features) {
            map.put(feature.key(), feature);
        }

        return map;
    }

    protected Version detectVersion() {
        return new Version(1, 6, -1, "beta-1");
    }

    public String name() {
        return "Groovy v" + InvokerHelper.getVersion();
    }
}