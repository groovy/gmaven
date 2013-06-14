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

import com.google.common.collect.Maps;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.gmaven.adapter.ClassSource;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.codehaus.gmaven.adapter.ScriptExecutor;
import org.codehaus.gmaven.plugin.util.PropertiesBuilder;
import org.codehaus.gmaven.plugin.util.SystemNoExitGuard;
import org.codehaus.gmaven.plugin.util.SystemNoExitGuard.Task;

import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;

/**
 * Execute a Groovy script.
 *
 * <br/>
 * See <a href="execute.html">usage</a> for more details.
 *
 * @since 2.0
 */
@Mojo(name = "execute", requiresProject = false, threadSafe = true, requiresDependencyResolution = TEST)
public class ExecuteMojo
    extends RuntimeMojoSupport
{
  @Component
  private ClassSourceFactory classSourceFactory;

  /**
   * Source of the script to execute.
   *
   * <br/>
   * Allowed content flavors:
   * <ul>
   *   <li>File</li>
   *   <li>URL</li>
   *   <li>Inline script</li>
   * </ul>
   *
   * <br/>
   * When using inline scripts, be aware that Maven will interpolate this value early when configuring execution.
   * This may cause problems if the script is expecting {@code groovy.lang.GString} evaluation instead.
   *
   * <br/>
   * Scripts which make use of GString expressions should seriously consider using a File or URL source instead.
   */
  @Parameter(property = "source", required = true)
  private String source;

  /**
   * Execution property overrides.
   *
   * <br/>
   * Any property defined here will take precedence over any other definition.
   */
  @Parameter
  private Map<String, String> properties;

  /**
   * Execution property defaults.
   *
   * <br/>
   * Any property defined _elsewhere_ (project, user, system, etc) will take precedence over default values.
   */
  @Parameter
  private Map<String, String> defaults;

  @Override
  protected void run() throws Exception {
    final ClassSource classSource = classSourceFactory.create(source);
    log.debug("Class source: {}", classSource);

    final ResourceLoader resourceLoader = new MojoResourceLoader(getRuntimeRealm(), classSource, getScriptpath());
    final Map<String, Object> context = createContext();
    final Map<String, Object> options = Maps.newHashMap();
    final ScriptExecutor executor = getRuntime().createScriptExecutor();

    // Guard against system exit and automatically restore system streams
    new SystemNoExitGuard().run(new Task()
    {
      @Override
      public void run() throws Exception {
        Object result = executor.execute(classSource, getRuntimeRealm(), resourceLoader, context, options);
        log.debug("Result: {}", result);
      }
    });
  }

  @Override
  protected void customizeProperties(final PropertiesBuilder builder) {
    builder.setProperties(properties)
        .setDefaults(defaults);
  }
}
