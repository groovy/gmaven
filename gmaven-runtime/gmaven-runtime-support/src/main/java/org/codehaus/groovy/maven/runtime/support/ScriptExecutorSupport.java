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

package org.codehaus.groovy.maven.runtime.support;

import org.codehaus.groovy.maven.feature.ComponentException;
import org.codehaus.groovy.maven.feature.Configuration;
import org.codehaus.groovy.maven.feature.Feature;
import org.codehaus.groovy.maven.feature.support.ComponentSupport;
import org.codehaus.groovy.maven.runtime.ClassFactory;
import org.codehaus.groovy.maven.runtime.ScriptExecutor;
import org.codehaus.groovy.maven.runtime.util.Callable;
import org.codehaus.groovy.maven.runtime.util.ClassSource;
import org.codehaus.groovy.maven.runtime.util.MagicAttribute;
import org.codehaus.groovy.maven.runtime.util.ResourceLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Support for {@link ScriptExecutor} component implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class ScriptExecutorSupport
    extends ComponentSupport
    implements ScriptExecutor
{
    protected ScriptExecutorSupport(final Feature feature, final Configuration config) {
        super(feature, config);
    }

    protected ScriptExecutorSupport(final Feature feature) {
        super(feature);
    }

    protected abstract ClassFactory getClassFactory();

    public Object execute(final ClassSource source, final ClassLoader classLoader, final ResourceLoader resourceLoader, final Configuration context) throws Exception {
        assert source != null;
        assert classLoader != null;
        // resourceLoader may be null
        // context may be null

        // Create/load the class
        Class type = getClassFactory().create(source, classLoader, resourceLoader);

        // Create a new instance
        Object target = type.newInstance();

        // Apply the execution context if we have one
        if (context != null) {
            applyContext(target, context);
        }

        // And then execute
        return execute(target);
    }

    protected abstract Object createClosure(Callable target);

    protected abstract Object createMagicAttribute(MagicAttribute attr);

    //
    // NOTE: Using reflection here to invoke setProperty() and run() to avoid evil class loading problems.
    //

    protected void applyContext(final Object target, final Configuration context) {
        assert target != null;
        assert context != null;

        Method setter = lookupMethod(target.getClass(), "setProperty", new Class[] { String.class, Object.class });

        log.debug("Setting context:");

        // Install the context for the script
        for (Iterator iter = context.names().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            Object value = context.get(name);

            // Adapt Callable instances into Closures
            if (value instanceof Callable) {
                Callable c = (Callable) value;

                value = createClosure(c);
            }

            // Handle some uber hacks
            else if (value instanceof MagicAttribute) {
                MagicAttribute attr = (MagicAttribute) value;

                value = createMagicAttribute(attr);
            }

            log.debug("    {} -> {}", name, value);

            invokeMethod(target, setter, new Object[] { name, value });
        }
    }

    protected Object execute(final Object target) {
        assert target != null;

        Method run = lookupMethod(target.getClass(), "run", new Class[0]);

        //
        // TODO: Look at Groovysh to allow for more flexible script execution
        //

        return invokeMethod(target, run, new Object[0]);
    }

    //
    // Reflection Helpers
    //

    protected Method lookupMethod(final Class type, final String name, final Class[] sig) {
        assert type != null;
        assert name != null;
        assert sig != null;

        Method method;

        try {
            method = type.getMethod(name, sig);
        }
        catch (Exception e) {
            throw new ComponentException("Failed to lookup method '" + name + "()' on: " + type, e);
        }

        return method;
    }

    protected Object invokeMethod(final Object target, final Method method, final Object[] args) {
        assert target != null;
        assert method != null;
        assert args != null;

        log.debug("Invoking {} on {} with {}", new Object[] {method, target, Arrays.asList(args)});

        try {
            return method.invoke(target, args);
        }
        catch (InvocationTargetException e) {
            // Unroll ITE to reduce the trace noise
            throw new ComponentException(e.getTargetException());
        }
        catch (Exception e) {
            throw new ComponentException("Failed to invoke method '" + method + "' on target: " + target, e);
        }
    }
}