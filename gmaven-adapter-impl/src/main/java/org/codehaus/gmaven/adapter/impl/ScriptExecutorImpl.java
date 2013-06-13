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

import java.util.Map;

import javax.annotation.Nullable;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import org.codehaus.gmaven.adapter.ClassSource;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.codehaus.gmaven.adapter.ScriptExecutor;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link ScriptExecutor} implementation.
 *
 * @since 2.0
 */
public class ScriptExecutorImpl
    implements ScriptExecutor
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final GroovyRuntimeImpl runtime;

  public ScriptExecutorImpl(final GroovyRuntimeImpl runtime) {
    this.runtime = checkNotNull(runtime);
  }

  @Override
  @Nullable
  public Object execute(final ClassSource classSource,
                        final ClassLoader classLoader,
                        final ResourceLoader resourceLoader,
                        final Map<String, Object> context,
                        final @Nullable Map<String, Object> options)
      throws Exception
  {
    checkNotNull(classSource);
    checkNotNull(classLoader);
    checkNotNull(resourceLoader);
    checkNotNull(context);

    log.trace("Execute; class-source: {}, class-loader: {}, resource-loader: {}, context: {}",
        classSource, classLoader, resourceLoader, context);

    GroovyClassLoader gcl = runtime.createGroovyClassLoader(classLoader, resourceLoader);

    CompilerConfiguration cc = new CompilerConfiguration();

    // TODO: How, if at all, do we want to expose compiler configuration customizations?

    Binding binding = runtime.createBinding(context);
    GroovyShell shell = new GroovyShell(gcl, binding, cc);

    if (options != null) {
      configureOptions(shell, options);
    }

    GroovyCodeSource codeSource = runtime.createGroovyCodeSource(classSource);
    try {
      return shell.evaluate(codeSource);
    }
    finally {
      gcl.clearCache();
    }
  }

  private void configureOptions(final GroovyShell shell, final Map<String, Object> options) {
    // TODO
  }
}
