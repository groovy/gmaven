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
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.gmaven.adapter.GroovyRuntime;
import org.codehaus.gmaven.adapter.MagicContext;
import org.codehaus.gmaven.plugin.util.ContainerHelper;
import org.codehaus.gmaven.plugin.util.GroovyVersionHelper;
import org.codehaus.gmaven.plugin.util.MavenVersionHelper;
import org.codehaus.gmaven.plugin.util.PropertiesBuilder;
import org.codehaus.gmaven.plugin.util.VersionHelper;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support for {@link org.apache.maven.plugin.Mojo} implementations which require
 * {@link GroovyRuntime} and related facilities.
 *
 * @since 2.0
 */
public abstract class RuntimeMojoSupport
    extends MojoSupport
{
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

  // TODO: Support configuring via -Dscriptpath= for project-less execution?

  /**
   * Path to search for imported scripts.
   */
  @Parameter
  protected List<File> scriptpath;

  /**
   * Execution property overrides.
   *
   * Any property defined here will take precedence over any other definition.
   */
  @Parameter
  protected Map<String, String> properties;

  /**
   * Execution property defaults.
   *
   * Any property defined _elsewhere_ (project, user, system, etc) will take precedence over default values.
   */
  @Parameter
  protected Map<String, String> defaults;

  // TODO: classpath inclusions?

  //
  // Runtime state
  //

  /**
   * Detected base directory.
   */
  protected File basedir;

  /**
   * Class-world for script execution.
   */
  protected ClassWorld classWorld;

  /**
   * Class-realm for GMaven runtime.
   */
  protected ClassRealm runtimeRealm;

  /**
   * Merged execution properties.
   */
  protected Map<String, String> executionProperties;

  /**
   * GMaven runtime.
   */
  protected GroovyRuntime runtime;

  @Override
  protected void prepare() throws Exception {
    basedir = resolveBasedir();
    log.debug("Base directory: {}", basedir);

    classWorld = new ClassWorld();

    executionProperties = propertiesBuilder
        .setProject(project)
        .setSession(session)
        .setProperties(properties)
        .setDefaults(defaults)
        .build();

    ClassLoader parentCl = getClass().getClassLoader();

    ensureMavenCompatibility(parentCl);

    runtimeRealm = classWorld.newRealm("gmaven-runtime", parentCl);

    ensureGroovyComparability(runtimeRealm);

    runtime = groovyRuntimeFactory.create(runtimeRealm);

    configureClasspath(runtimeRealm);
  }

  /**
   * Resolves the base directory for the current execution.
   */
  private File resolveBasedir() throws IOException {
    String path = null;

    if (project != null) {
      File file = project.getBasedir();
      if (file != null) {
        path = file.getAbsolutePath();
      }
    }

    if (path == null) {
      path = session.getExecutionRootDirectory();
    }

    if (path == null) {
      path = System.getProperty("user.dir");
    }

    return new File(path).getCanonicalFile();
  }

  @Override
  protected void cleanup() throws Exception {
    if (runtime != null) {
      runtime.cleanup();
    }
    if (runtimeRealm != null) {
      classWorld.disposeRealm(runtimeRealm.getId());
      runtimeRealm = null;
    }
    classWorld = null;
    executionProperties = null;
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
   * Configure additional classpath elements for the runtime realm.
   */
  private void configureClasspath(final ClassRealm realm) {
    // TODO:
  }

  /**
   * Configures the context which will be available to executed scripts as binding variables.
   */
  protected Map<String, Object> createContext() {
    Map<String, Object> context = Maps.newHashMap();

    // TODO: Include mojo execution ID in logger name?  Would have to sanitize it first, may not be worth it

    String loggerName = String.format("%s.%s.Script", project.getGroupId(), project.getArtifactId());
    Logger logger = LoggerFactory.getLogger(loggerName);
    context.put("log", logger);

    context.put("container", containerHelper);
    context.put("plugin", pluginDescriptor);
    context.put("mojo", mojoExecution);
    context.put("basedir", basedir);
    context.put("project", project);
    context.put("properties", executionProperties);
    context.put("session", session);
    context.put("settings", settings);
    context.put("ant", MagicContext.ANT_BUILDER);
    context.put("fail", new FailClosureTarget());

    return context;
  }
}
