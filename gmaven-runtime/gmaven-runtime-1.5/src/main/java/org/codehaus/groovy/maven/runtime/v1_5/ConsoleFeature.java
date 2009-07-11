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

package org.codehaus.groovy.maven.runtime.v1_5;

import groovy.lang.Binding;
import org.codehaus.groovy.maven.common.StreamPair;
import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.feature.support.ComponentSupport;
import org.codehaus.groovy.maven.feature.support.FeatureSupport;
import org.codehaus.groovy.maven.runtime.Console;
import org.codehaus.groovy.maven.runtime.support.util.NoExitSecurityManager;

import java.util.EventObject;

/**
 * Provides the GUI console feature.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ConsoleFeature
    extends FeatureSupport
{
    public ConsoleFeature() {
        super(Console.KEY);
    }

    protected Component doCreate() throws Exception {
        return new ConsoleImpl();
    }

    //
    // ConsoleImpl
    //

    private class ConsoleImpl
        extends ComponentSupport
        implements Console
    {
        private final Object lock = new Object();

        private ConsoleImpl() throws Exception {
            super(ConsoleFeature.this);
        }

        public void execute(final ClassLoader classLoader) throws Exception {
            assert classLoader != null;

            final StreamPair streams = StreamPair.system();

            final SecurityManager sm = System.getSecurityManager();

            System.setSecurityManager(new NoExitSecurityManager());

            try {
                final groovy.ui.Console console = new groovy.ui.Console(classLoader, new Binding()) {
                    public void exit(final EventObject event) {
                        try {
                            super.exit(event);
                        }
                        finally {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                    }
                };

                console.run();

                synchronized (lock) {
                    lock.wait();
                }
            }
            finally {
                System.setSecurityManager(sm);
                StreamPair.system(streams);
            }
        }
    }
}