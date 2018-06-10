/*
 * Copyright (c) 2006-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.gmaven.adapter.impl;

import java.util.EventObject;
import java.util.Map;

import javax.annotation.Nullable;

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

  ConsoleWindowImpl(final GroovyRuntimeImpl runtime) {
    this.runtime = checkNotNull(runtime);
  }

  @Override
  public WindowHandle open(final ClassLoader classLoader,
                           final ResourceLoader resourceLoader,
                           final Map<String, Object> context,
                           final @Nullable Map<String, Object> options)
      throws Exception
  {
    checkNotNull(classLoader);
    checkNotNull(resourceLoader);
    checkNotNull(context);

    log.trace("Opening; class-loader: {}, resource-loader: {}, context: {}",
        classLoader, resourceLoader, context);

    final GroovyClassLoader gcl = runtime.createGroovyClassLoader(classLoader, resourceLoader);
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

    if (options != null) {
      configureOptions(console, options);
    }

    log.trace("Opening");
    console.run();

    return new WindowHandle()
    {
      @Override
      public void close() {
        log.trace("Closing");
        console.exit();
        cleanup();
      }

      @Override
      public void await() throws InterruptedException {
        log.trace("Waiting");
        synchronized (lock) {
          lock.wait();
        }
        cleanup();
      }

      private void cleanup() {
        gcl.clearCache();
      }
    };
  }

  private void configureOptions(final Console console, final Map<String, Object> options) {
    // TODO
  }
}
