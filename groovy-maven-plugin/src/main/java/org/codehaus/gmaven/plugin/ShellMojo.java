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

import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.codehaus.gmaven.adapter.ShellRunner;

import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;

/**
 * Run {@code groovysh} shell.
 *
 * <br/>
 * See <a href="shell.html">usage</a> for more details.
 *
 * @since 2.0
 */
@Mojo(name = "shell", requiresProject = false, requiresDependencyResolution = TEST, aggregator = true)
public class ShellMojo
    extends RuntimeMojoSupport
{
  // TODO: Expose groovysh options

  @Override
  protected void run() throws Exception {
    final ResourceLoader resourceLoader = new MojoResourceLoader(getRuntimeRealm(), getScriptpath());
    final Map<String, Object> context = createContext();
    final Map<String, Object> options = new HashMap<String, Object>();
    final ShellRunner shell = getRuntime().createShellRunner();

    shell.run(getRuntimeRealm(), resourceLoader, context, options);
  }
}
