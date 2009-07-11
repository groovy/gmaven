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

package org.codehaus.groovy.maven.feature.support;

import org.codehaus.groovy.maven.feature.Configuration;
import org.codehaus.groovy.maven.feature.Feature;
import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;

/**
 * Implements a {@link Provider} which delegates to another {@link Provider} instance.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DelegatingProvider
    implements Provider
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private Provider delegate;

    public DelegatingProvider(final Provider delegate) {
        // delegate may be null
        
        this.delegate = delegate;
    }

    public DelegatingProvider(final ClassLoader classLoader, final InvocationHandler handler) {
        this(createProxy(classLoader, handler));
    }

    public DelegatingProvider(final InvocationHandler handler) {
        this(createProxy(handler));
    }

    protected DelegatingProvider() {
        this((Provider)null);
    }

    public synchronized Provider getDelegate() {
        if (delegate == null) {
            try {
                delegate = findDelegate();

                log.trace("Found delegate: {}", delegate);
            }
            catch (Exception e) {
                log.debug("Failed to find delegate: " + e, e);
            }
        }

        return delegate;
    }

    protected Provider findDelegate() throws Exception {
        throw new InternalError("No delegate bound; Provide a delegate instance or override 'findDelegate()'");
    }

    public String key() {
        return getDelegate().key();
    }

    public String name() {
        return getDelegate().name();
    }

    public Version version() {
        return getDelegate().version();
    }

    public boolean supported() {
        return getDelegate().supported();
    }

    public void require() {
        getDelegate().require();
    }

    public Configuration config() {
        return getDelegate().config();
    }

    public Collection features() {
        return getDelegate().features();
    }

    public Feature feature(final String key) {
        return getDelegate().feature(key);
    }

    public Feature feature(final Class key) {
        return getDelegate().feature(key);
    }

    public int hashCode() {
        return getDelegate().hashCode();
    }

    /** @noinspection EqualsWhichDoesntCheckParameterClass */
    public boolean equals(final Object obj) {
        return getDelegate().equals(obj);
    }

    public String toString() {
        return getDelegate().toString();
    }

    //
    // Proxy Helpers
    //

    private static final Class[] PROXY_INTERFACES = {
        Provider.class,
    };

    public static Provider createProxy(final ClassLoader classLoader, final InvocationHandler handler) {
        assert classLoader != null;
        assert handler != null;

        return (Provider) Proxy.newProxyInstance(classLoader, PROXY_INTERFACES, handler);
    }

    public static Provider createProxy(final InvocationHandler handler) {
        return createProxy(Thread.currentThread().getContextClassLoader(), handler);
    }
}