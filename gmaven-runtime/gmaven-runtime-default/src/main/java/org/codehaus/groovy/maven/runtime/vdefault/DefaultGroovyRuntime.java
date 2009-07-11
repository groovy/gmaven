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

package org.codehaus.groovy.maven.runtime.vdefault;

import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.ProviderSelector;
import org.codehaus.groovy.maven.feature.support.DelegatingProvider;
import org.codehaus.groovy.maven.feature.support.ProviderSupport;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Provides the <em>default</em> Groovy runtime.
 *
 * @plexus.component role="org.codehaus.groovy.maven.feature.Provider" role-hint="default"
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DefaultGroovyRuntime
    extends DelegatingProvider
    implements Contextualizable
{
    public static final String KEY = ProviderSelector.SELECT_DEFAULT;

    public static final String DELEGATE_KEY = "1.5";

    private PlexusContainer container;

    public DefaultGroovyRuntime() {}

    public void contextualize(final Context context) throws ContextException {
        assert context != null;

        container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }

    protected PlexusContainer getContainer() {
        if (container == null) {
            throw new IllegalStateException("Container not bound");
        }

        return container;
    }

    protected Provider findDelegate() throws Exception {
        return createProxy(new InvocationHandler() {
            private Provider target;

            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                assert proxy != null;
                assert method != null;
                // args can be null
                
                if (target == null) {
                    target = (Provider) getContainer().lookup(Provider.class.getName(), DELEGATE_KEY);

                    log.debug("Target provider: {}", target);
                }

                return method.invoke(target, args);
            }
        });
    }

    public String key() {
        return KEY;
    }

    public String toString() {
        return ProviderSupport.asString(this);
    }
}