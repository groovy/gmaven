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
package org.codehaus.gmaven.plugin;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.gmaven.adapter.ClassSource;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.codehaus.gmaven.adapter.ScriptExecutor;
import org.codehaus.gmaven.plugin.util.PropertiesBuilder;

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


  /**
   * Skip the execution of this mojo.
   *
   * @since 2.2.0
   */
  @Parameter(property = "gmaven.execute.skip", defaultValue = "false")
  private boolean skip;

  @Override
  protected void run() throws Exception {
    final ClassSource classSource = classSourceFactory.create(source);
    log.debug("Class source: {}", classSource);

    final ResourceLoader resourceLoader = new MojoResourceLoader(getRuntimeRealm(), classSource, getScriptpath());
    final Map<String, Object> context = createContext();
    final Map<String, Object> options = new HashMap<String, Object>();
    final ScriptExecutor executor = getRuntime().createScriptExecutor();

    Object result = executor.execute(classSource, getRuntimeRealm(), resourceLoader, context, options);
    log.debug("Result: {}", result);
  }

  @Override
  protected void customizeProperties(final PropertiesBuilder builder) {
    builder.setProperties(properties)
        .setDefaults(defaults);
  }

  @Override
  protected boolean isSkipped() {
    return skip;
  }

}
