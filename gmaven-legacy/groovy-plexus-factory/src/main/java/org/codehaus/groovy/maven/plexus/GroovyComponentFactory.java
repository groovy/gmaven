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

package org.codehaus.groovy.maven.plexus;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.ProviderManager;
import org.codehaus.groovy.maven.runtime.ClassFactory;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 * Factory for loading components implemented in <a href="http://groovy.codehaus.org">Groovy</a>.
 *
 * @plexus.component role="org.codehaus.plexus.component.factory.ComponentFactory" role-hint="groovy"
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class GroovyComponentFactory
    extends AbstractComponentFactory
{
    /**
     * @plexus.requirement
     *
     * @noinspection UnusedDeclaration
     */
    private ProviderManager providerManager;

    public Object newInstance(final ComponentDescriptor component, final ClassRealm realm, final PlexusContainer container)
        throws ComponentInstantiationException
    {
        assert component != null;
        assert realm != null;

        try {
            Provider provider = providerManager.select();

            ClassFactory factory = (ClassFactory) provider.feature(ClassFactory.KEY).create();

            ClassLoader classLoader = realm.getClassLoader();

            Class type = factory.create(component.getImplementation(), classLoader);

            return type.newInstance();
        }
        catch (Exception e) {
            throw new ComponentInstantiationException("Failed to create Groovy component: " + e, e);
        }
    }
}
