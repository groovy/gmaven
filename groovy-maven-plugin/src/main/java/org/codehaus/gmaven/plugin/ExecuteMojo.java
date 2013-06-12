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

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.gmaven.adapter.ClassSource;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.codehaus.gmaven.adapter.ScriptExecutor;

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

  /**
   * Source of the script to execute.
   *
   * Can be one of: URL, File or inline script.
   *
   * When using inline scripts, be aware that Maven will interpolate this value early when configuring execution.
   * This may cause problems if the script is expecting {@code groovy.lang.GString} evaluation instead.
   * Scripts which make use of GString expressions should seriously consider using a File or URL source instead.
   */
  @Parameter(property = "source", required = true)
  private String source;

  @Override
  protected void run() throws Exception {
    ClassSource classSource = classSourceFactory.create(source);
    log.debug("Class source: {}", classSource);

    ResourceLoader resourceLoader = new MojoResourceLoader(runtimeRealm, classSource, scriptpath);
    Map<String, Object> context = createContext();
    ScriptExecutor executor = runtime.getScriptExecutor();

    Object result = executor.execute(classSource, runtimeRealm, resourceLoader, context);
    log.debug("Result: {}", result);
  }
}
