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

import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.gmaven.adapter.ConsoleWindow;
import org.codehaus.gmaven.adapter.ConsoleWindow.WindowHandle;
import org.codehaus.gmaven.adapter.ResourceLoader;

import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;

/**
 * Open a Groovy console window.
 *
 * <br/>
 * See <a href="console.html">usage</a> for more details.
 *
 * @since 2.0
 */
@Mojo(name = "console", requiresProject = false, requiresDependencyResolution = TEST, aggregator = true)
public class ConsoleMojo
    extends RuntimeMojoSupport
{
  // TODO: Expose console options

  @Override
  protected void run() throws Exception {
    final ResourceLoader resourceLoader = new MojoResourceLoader(getRuntimeRealm(), getScriptpath());
    final Map<String, Object> context = createContext();
    final Map<String, Object> options = Maps.newHashMap();
    final ConsoleWindow console = getRuntime().createConsoleWindow();

    WindowHandle handle = console.open(getRuntimeRealm(), resourceLoader, context, options);
    handle.await();
  }
}
