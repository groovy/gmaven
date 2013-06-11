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

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.gmaven.adapter.ClassSource;
import org.codehaus.gmaven.adapter.GroovyRuntime;
import org.codehaus.gmaven.adapter.MagicContext;
import org.codehaus.gmaven.adapter.ResourceLoader;
import org.codehaus.gmaven.adapter.ScriptExecutor;
import org.codehaus.gmaven.plugin.util.ContainerHelper;
import org.codehaus.gmaven.plugin.util.GroovyVersionHelper;
import org.codehaus.gmaven.plugin.util.MavenVersionHelper;
import org.codehaus.gmaven.plugin.util.PropertiesBuilder;
import org.codehaus.gmaven.plugin.util.VersionHelper;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execute a Groovy script.
 *
 * @since 2.0
 */
@Mojo(name = "execute", threadSafe = true)
public class ExecuteMojo
    extends MojoSupport
{
  // TODO: Shall we use -Dsource or -Dgmaven.source ?

  private static final String GROOVY_SOURCE = "groovy.source";

  private static final String GROOVY_SOURCE_EXPR = "${" + GROOVY_SOURCE + "}";

  // TODO: Will need to have separate goals to have project/non-project execution support

  @Component
  private PluginDescriptor pluginDescriptor;

  @Component
  private MojoExecution mojoExecution;

  @Component
  private MavenProject project;

  @Component
  private MavenSession session;

  @Component
  private Settings settings;

  @Component
  private ContainerHelper containerHelper;

  @Component
  private VersionHelper versionHelper;

  @Component
  private MavenVersionHelper mavenVersionHelper;

  @Component
  private GroovyVersionHelper groovyVersionHelper;

  @Component
  private GroovyRuntimeFactory groovyRuntimeFactory;

  @Component
  private PropertiesBuilder propertiesBuilder;

  //
  // Configuration
  //

  @Parameter(property = GROOVY_SOURCE)
  private PlexusConfiguration source;

  // TODO: Make this a PlexusConfiguration so we can have -Dscriptpath= support?

  @Parameter
  private File[] scriptpath;

  // TODO: properties, defaults, classpath inclusions?

  //
  // Runtime state
  //

  /**
   * Class-world for script execution.
   */
  private ClassWorld classWorld;

  /**
   * Class-realm for GMaven runtime.
   */
  private ClassRealm runtimeRealm;

  /**
   * Merged execution properties.
   */
  private Map<String, String> properties;

  @Override
  protected void prepare() throws Exception {
    classWorld = new ClassWorld();

    properties = propertiesBuilder
        .setProject(project)
        .setSession(session)
        .build();
  }

  @Override
  protected void run() throws Exception {
    ClassLoader parentCl = getClass().getClassLoader();

    ensureMavenCompatibility(parentCl);

    runtimeRealm = classWorld.newRealm("gmaven-runtime", parentCl);

    ensureGroovyComparability(runtimeRealm);

    GroovyRuntime runtime = groovyRuntimeFactory.create(runtimeRealm);

    String script = decodeScript(source);
    log.debug("Script: {}", script);

    ClassSource classSource = ClassSource.create(script);
    log.debug("Class source: {}", classSource);

    ResourceLoader resourceLoader = new MojoResourceLoader(runtimeRealm, classSource, scriptpath);
    Map<String, Object> context = createContext();
    ScriptExecutor executor = runtime.getScriptExecutor();

    Object result = executor.execute(classSource, runtimeRealm, resourceLoader, context);
    log.debug("Result: {}", result);
  }

  @Override
  protected void cleanup() throws Exception {
    if (runtimeRealm != null) {
      classWorld.disposeRealm(runtimeRealm.getId());
    }
  }

  /**
   * Ensure Maven compatibility.  Requires Maven 3+
   */
  private void ensureMavenCompatibility(final ClassLoader classLoader) throws MojoExecutionException {
    Version mavenVersion = mavenVersionHelper.detectVersion(classLoader);
    if (mavenVersion == null) {
      // complain and continue
      log.error("Unable to determine Maven version");
    }
    else {
      log.debug("Detected Maven version: {}", mavenVersion);
      if (versionHelper.before(3).containsVersion(mavenVersion)) {
        throw new MojoExecutionException("Unsupported Maven version: " + mavenVersion);
      }
    }
  }

  /**
   * Ensure Groovy compatibility.  Requires Groovy 2+
   */
  private void ensureGroovyComparability(final ClassLoader classLoader) throws MojoExecutionException {
    Version groovyVersion = groovyVersionHelper.detectVersion(classLoader);
    if (groovyVersion == null) {
      // complain and continue
      log.error("Unable to determine Groovy version");
    }
    else {
      log.debug("Detected Groovy version: {}", groovyVersion);
      if (versionHelper.before(2).containsVersion(groovyVersion)) {
        throw new MojoExecutionException("Unsupported Groovy version: " + groovyVersion);
      }
    }
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
    if (GROOVY_SOURCE_EXPR.equals(script)) {
      if (!properties.containsKey(GROOVY_SOURCE)) {
        throw new MojoExecutionException("Missing <source> parameter or ${groovy.source} property");
      }
      script = properties.get(GROOVY_SOURCE);
    }

    return script;
  }

  /**
   * Configures the context which will be available to executed scripts as binding variables.
   */
  private Map<String, Object> createContext() {
    Map<String, Object> context = Maps.newHashMap();

    // TODO: Include mojo execution ID in logger name?

    String loggerName = String.format("%s.%s.Script", project.getGroupId(), project.getArtifactId());
    Logger logger = LoggerFactory.getLogger(loggerName);
    context.put("log", logger);

    // TODO: May need to adapt project and properties to auto-resolve?
    // TODO: This may only be required if we add custom properties/defaults configuration support?

    context.put("container", containerHelper);
    context.put("plugin", pluginDescriptor);
    context.put("mojo", mojoExecution);
    context.put("project", project);
    context.put("properties", properties);
    context.put("session", session);
    context.put("settings", settings);
    context.put("ant", MagicContext.ANT_BUILDER);
    context.put("fail", new FailClosureTarget());

    return context;
  }
}
