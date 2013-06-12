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

package org.codehaus.gmaven.plugin;

import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.gmaven.adapter.ClassSource;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.codehaus.gmaven.adapter.ScriptExecutor;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * Execute a Groovy script.
 *
 * @since 2.0
 */
@Mojo(name = "execute", threadSafe = true)
public class ExecuteMojo
    extends RuntimeMojoSupport
{
  // TODO: Will need to have separate goals to have project/non-project execution support
  // TODO: Or is there any way we can have a single mojo which can support both?

  @Component
  private ClassSourceFactory classSourceFactory;

  //
  // Configuration
  //

  private static final String SOURCE = "source";

  private static final String SOURCE_EXPR = "${" + SOURCE + "}";

  // FIXME: Perhaps revert back to using a String here, since we can not easily instruct Maven to not interpolate,
  // FIXME: ... which was what I was attempting to do here with this special type.

  /**
   * Source of the script to execute.
   *
   * Can be one of: URL, File or inline script.
   *
   * When using inline scripts, be aware that Maven will interpolate this value early when configuring execution.
   * This may cause problems if the script is expecting {@code groovy.lang.GString} evaluation instead.
   * Scripts which make use of GString expressions should seriously consider using a File or URL source instead.
   */
  @Parameter(property = SOURCE)
  private PlexusConfiguration source;

  @Override
  protected void run() throws Exception {
    String script = decodeScript(source);
    log.debug("Script: {}", script);

    ClassSource classSource = classSourceFactory.create(script);
    log.debug("Class source: {}", classSource);

    ResourceLoader resourceLoader = new MojoResourceLoader(runtimeRealm, classSource, scriptpath);
    Map<String, Object> context = createContext();
    ScriptExecutor executor = runtime.getScriptExecutor();

    Object result = executor.execute(classSource, runtimeRealm, resourceLoader, context);
    log.debug("Result: {}", result);
  }

  /**
   * Decode the text/location of the script to execute.
   *
   * Configuration must be a simple value, no nested xml elements.
   *
   * If left as default, then script value is pulled from the {@code groovy.script} property if it has been set.
   */
  private String decodeScript(final PlexusConfiguration configuration) throws MojoExecutionException {
    log.debug("Decoding script from: {}", configuration);
    String script = source.getValue();

    // source must be simple container
    if (source.getChildCount() != 0) {
      throw new MojoExecutionException("Invalid <source> parameter contents; contains nested elements");
    }

    // if the source value is the default property expression, then attempt to resolve it
    if (SOURCE_EXPR.equals(script)) {
      if (!executionProperties.containsKey(SOURCE)) {
        throw new MojoExecutionException("Missing <source> parameter or " + SOURCE_EXPR + " property");
      }
      script = executionProperties.get(SOURCE);
    }

    return script;
  }
}
