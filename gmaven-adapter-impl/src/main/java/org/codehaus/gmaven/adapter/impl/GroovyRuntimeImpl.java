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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Throwables;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyResourceLoader;
import groovy.util.AntBuilder;
import org.apache.tools.ant.BuildLogger;
import org.codehaus.gmaven.adapter.ClassSource;
import org.codehaus.gmaven.adapter.ClassSource.Inline;
import org.codehaus.gmaven.adapter.ClosureTarget;
import org.codehaus.gmaven.adapter.ConsoleWindow;
import org.codehaus.gmaven.adapter.GroovyRuntime;
import org.codehaus.gmaven.adapter.MagicContext;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.codehaus.gmaven.adapter.ScriptExecutor;
import org.codehaus.gmaven.adapter.ShellRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link GroovyRuntime} implementation.
 *
 * @since 2.0
 */
public class GroovyRuntimeImpl
    implements GroovyRuntime
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public ScriptExecutor getScriptExecutor() {
    return new ScriptExecutorImpl(this);
  }

  @Override
  public ConsoleWindow getConsoleWindow() {
    return new ConsoleWindowImpl(this);
  }

  @Override
  public ShellRunner getShellRunner() {
    return new ShellRunnerImpl(this);
  }

  @Override
  public void cleanup() {
    // nothing atm
  }

  //
  // Internal
  //

  /**
   * Create a {@link GroovyClassLoader} from given {@link ClassLoader} and {@link ResourceLoader}.
   */
  public GroovyClassLoader createGroovyClassLoader(final ClassLoader classLoader, final ResourceLoader resourceLoader) {
    GroovyClassLoader gcl = new GroovyClassLoader(classLoader);
    gcl.setResourceLoader(createGroovyResourceLoader(resourceLoader));
    return gcl;
  }

  /**
   * Creates a {@link GroovyResourceLoader} from a {@link ResourceLoader}.
   */
  public GroovyResourceLoader createGroovyResourceLoader(final ResourceLoader resourceLoader) {
    checkNotNull(resourceLoader);

    return new GroovyResourceLoader()
    {
      @Override
      public URL loadGroovySource(final String name) throws MalformedURLException {
        return resourceLoader.loadResource(name);
      }
    };
  }

  /**
   * Creates a {@link GroovyCodeSource} from a {@link ClassSource}.
   */
  public GroovyCodeSource createGroovyCodeSource(final ClassSource source) throws IOException {
    checkNotNull(source);

    if (source.getUrl() != null) {
      return new GroovyCodeSource(source.getUrl());
    }
    if (source.getFile() != null) {
      return new GroovyCodeSource(source.getFile());
    }
    if (source.getInline() != null) {
      Inline inline = source.getInline();
      return new GroovyCodeSource(inline.getInput(), inline.getName(), inline.getCodeBase());
    }

    throw new Error("Unable to create GroovyCodeSource from: " + source);
  }

  /**
   * Creates a {@link Closure} from a {@link ClosureTarget}.
   */
  public Closure createClosure(final Object owner, final ClosureTarget target) {
    checkNotNull(owner);
    checkNotNull(target);

    return new Closure(owner)
    {
      @Override
      public Object call(final Object[] args) {
        try {
          return target.call(args);
        }
        catch (Exception e) {
          throw Throwables.propagate(e);
        }
      }

      @Override
      public String toString() {
        return Closure.class.getSimpleName() + "{owner=" + owner + ", target=" + target + "}";
      }
    };
  }

  /**
   * Create an object for a {@link MagicContext} entry.
   */
  public Object createMagicContextValue(final MagicContext magic) {
    checkNotNull(magic);

    switch (magic) {
      // Create the AntBuilder instance, normalizing its output to match mavens
      case ANT_BUILDER: {
        AntBuilder ant = new AntBuilder();
        // TODO: Do we need to root the ant-project or otherwise augment the configuration?

        Object obj = ant.getAntProject().getBuildListeners().elementAt(0);
        if (obj instanceof BuildLogger) {
          BuildLogger logger = (BuildLogger) obj;
          logger.setEmacsMode(true);
        }
        return ant;
      }
    }

    throw new Error("Unsupported magic context: " + magic);
  }

  /**
   * Create script binding, handling conversion of {@link ClosureTarget} and {@link MagicContext} entries.
   */
  public Binding createBinding(final Map<String, Object> context) {
    Binding binding = new Binding();

    log.debug("Binding:");
    for (Entry<String, Object> entry : context.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      if (value instanceof ClosureTarget) {
        value = createClosure(this, (ClosureTarget) value);
      }
      else if (value instanceof MagicContext) {
        value = createMagicContextValue((MagicContext) value);
      }

      log.debug("  {}={}", key, value);
      binding.setVariable(entry.getKey(), value);
    }

    return binding;
  }
}
