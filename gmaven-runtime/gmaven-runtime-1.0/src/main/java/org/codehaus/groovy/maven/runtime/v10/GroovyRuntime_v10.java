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

package org.codehaus.groovy.maven.runtime.v10;

import java.util.Map;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.maven.feature.Version;
import org.codehaus.groovy.maven.feature.support.ProviderSupport;

/**
 * Provides support for Groovy 1.0.
 *
 * @plexus.component role="org.codehaus.groovy.maven.feature.Provider" role-hint="1.0"
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class GroovyRuntime_v10
    extends ProviderSupport
{
    public static final String KEY = "1.0";

    /**
     * @plexus.requirement role="org.codehaus.groovy.maven.feature.Feature#1.0"
     *
     * @noinspection UnusedDeclaration,MismatchedQueryAndUpdateOfCollection
     */
    private Map features;

    public GroovyRuntime_v10() {
        super(KEY);
    }

    protected Map detectFeatures() {
        return features;
    }

    protected Version detectVersion() {
        return new Version(1, 0);
    }

    public String name() {
        return "Groovy v" + InvokerHelper.getVersion();
    }
}