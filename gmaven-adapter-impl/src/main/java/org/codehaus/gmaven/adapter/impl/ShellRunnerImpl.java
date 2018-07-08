/*
 * Copyright (c) 2006-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.gmaven.adapter.impl;

import java.util.Map;

import javax.annotation.Nullable;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.codehaus.gmaven.adapter.ShellRunner;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link ShellRunner} implementation.
 *
 * @since 2.0
 */
public class ShellRunnerImpl
    implements ShellRunner
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final GroovyRuntimeImpl runtime;

  ShellRunnerImpl(final GroovyRuntimeImpl runtime) {
    this.runtime = checkNotNull(runtime);
  }

  @Override
  public void run(final ClassLoader classLoader,
                  final ResourceLoader resourceLoader,
                  final Map<String, Object> context,
                  final @Nullable Map<String, Object> options)
      throws Exception
  {
    checkNotNull(classLoader);
    checkNotNull(resourceLoader);
    checkNotNull(context);

    log.trace("Running; class-loader: {}, resource-loader: {}, context: {}",
        classLoader, resourceLoader, context);

    GroovyClassLoader gcl = runtime.createGroovyClassLoader(classLoader, resourceLoader);
    Binding binding = runtime.createBinding(context);

    Groovysh shell = new Groovysh(gcl, binding, new IO());

    if (options != null) {
      configureOptions(shell, options);
    }

    try {
      shell.run();
    }
    finally {
      gcl.clearCache();
    }
  }

  private void configureOptions(final Groovysh shell, final Map<String, Object> options) {
    // TODO:
  }
}
