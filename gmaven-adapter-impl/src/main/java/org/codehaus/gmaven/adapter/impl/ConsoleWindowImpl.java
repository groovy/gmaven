/*
 * Copyright (c) 2007-2013, the original author or authors.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

package org.codehaus.gmaven.adapter.impl;

import java.util.EventObject;
import java.util.Map;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.ui.Console;
import org.codehaus.gmaven.adapter.ConsoleWindow;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link ConsoleWindow} implementation.
 *
 * @since 2.0
 */
public class ConsoleWindowImpl
    implements ConsoleWindow
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final GroovyRuntimeImpl runtime;

  private final Object lock = new Object();

  public ConsoleWindowImpl(final GroovyRuntimeImpl runtime) {
    this.runtime = checkNotNull(runtime);
  }

  @Override
  public WindowHandle open(final ClassLoader classLoader,
                           final ResourceLoader resourceLoader,
                           final Map<String, Object> context)
      throws Exception
  {
    checkNotNull(classLoader);
    checkNotNull(resourceLoader);
    checkNotNull(context);

    log.trace("Opening; class-loader: {}, resource-loader: {}, context: {}",
        classLoader, resourceLoader, context);

    GroovyClassLoader gcl = runtime.create(classLoader, resourceLoader);
    Binding binding = runtime.createBinding(context);

    // FIXME: Sort out how we can avoid IDEA from thinking a sub-class of this needs to have additional overrides
    // FIXME: Its complaining about the synthetic methods which are dynamically added to GroovyObject instances
    // FIXME: Perhaps we can install exit lock notification via an event listener instead?

    final Console console = new Console(gcl, binding)
    {
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

    log.trace("Opening");
    console.run();

    return new WindowHandle()
    {
      @Override
      public void close() {
        log.trace("Closing");
        console.exit();
      }

      @Override
      public void await() throws InterruptedException {
        log.trace("Waiting");
        synchronized (lock) {
          lock.wait();
        }
      }
    };
  }
}
