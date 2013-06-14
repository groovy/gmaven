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
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.maven.artifact.Artifact;
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

import static org.codehaus.gmaven.plugin.ClasspathScope.compile;
import static org.codehaus.gmaven.plugin.ClasspathScope.test;

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

  /**
   * Include additional classpath from project artifacts in given scope.
   *
   * <br/>
   * Scope can be one of:
   * <ul>
   *   <li>none</li>
   *   <li>provided</li>
   *   <li>compile</li>
   *   <li>runtime</li>
   *   <li>test</li>
   * </ul>
   */
  @Parameter(property = "scope", defaultValue = "none")
  private ClasspathScope classpathScope;

  /**
   * Path to search for imported scripts.
   *
   * <br/>
   * Supports -Dscriptpath=PATH,PATH property syntax.
   */
  @Parameter(property = "scriptpath")
  private List<File> scriptpath;

  protected List<File> getScriptpath() {
    return scriptpath;
  }

  //
  // Runtime state
  //

  /**
   * Detected base directory.
   */
  private File basedir;

  /**
   * Class-world for script execution.
   */
  private ClassWorld classWorld;

  /**
   * Class-realm for GMaven runtime.
   */
  private ClassRealm runtimeRealm;

  protected ClassRealm getRuntimeRealm() {
    return runtimeRealm;
  }

  /**
   * GMaven runtime.
   */
  private GroovyRuntime runtime;

  protected GroovyRuntime getRuntime() {
    return runtime;
  }

  @Override
  protected void prepare() throws Exception {
    basedir = resolveBasedir();
    log.debug("Base directory: {}", basedir);

    classWorld = new ClassWorld();

    ClassLoader parentCl = getClass().getClassLoader();

    ensureMavenCompatibility(parentCl);

    runtimeRealm = classWorld.newRealm("gmaven-runtime", parentCl);

    ensureGroovyComparability(runtimeRealm);

    runtime = groovyRuntimeFactory.create(runtimeRealm);

    configureAdditionalClasspath(runtimeRealm);
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
    // allow runtime to clean up
    if (runtime != null) {
      runtime.cleanup();
    }

    // dispose runtime realm
    if (runtimeRealm != null) {
      classWorld.disposeRealm(runtimeRealm.getId());
      runtimeRealm = null;
    }

    // sanity nulls
    classWorld = null;
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
  private void configureAdditionalClasspath(final ClassRealm realm) {
    log.debug("Configuring additional classpath with scope: {}", classpathScope);

    List<File> classpath = Lists.newArrayList();

    // add build output directory if scope includes 'compile'
    if (classpathScope.matches(compile)) {
      classpath.add(new File(project.getBuild().getOutputDirectory()));
    }

    // add build test output directory if scope includes 'test'
    if (classpathScope.matches(test)) {
      classpath.add(new File(project.getBuild().getTestOutputDirectory()));
    }

    // add matching project dependency artifacts
    if (!project.getArtifacts().isEmpty()) {
      for (Artifact artifact : project.getArtifacts()) {
        if (classpathScope.matches(artifact.getScope())) {
          File file = artifact.getFile();
          if (!file.exists()) {
            // for sanity, this should not really ever happen
            log.warn("Artifact not resolved; ignoring: {}", artifact);
            continue;
          }
          classpath.add(file);
        }
      }
    }

    if (!classpath.isEmpty()) {
      log.debug("Additional classpath:");
      for (File file : classpath) {
        log.debug("  {}", file);
        try {
          realm.addURL(file.toURI().toURL());
        }
        catch (MalformedURLException e) {
          throw Throwables.propagate(e);
        }
      }
    }
  }

  /**
   * Create script logger.
   */
  private Logger createLogger() {
    String loggerName = String.format("%s.%s.Script", project.getGroupId(), project.getArtifactId());
    return LoggerFactory.getLogger(loggerName);
  }

  /**
   * Create script execution properties.
   */
  private Properties createProperties() {
    propertiesBuilder
      .setProject(project)
      .setSession(session);

    customizeProperties(propertiesBuilder);

    // convert to Properties for better compatibility, groovy doesn't care about types so its not a big deal
    Properties props = new Properties();
    props.putAll(propertiesBuilder.build());
    return props;
  }

  /**
   * Allow sub-class to customize properties.
   */
  protected void customizeProperties(final PropertiesBuilder builder) {
    // empty
  }

  /**
   * Configures the context which will be available to executed scripts as binding variables.
   */
  protected Map<String, Object> createContext() {
    Map<String, Object> context = Maps.newHashMap();

    context.put("log", createLogger());
    context.put("container", containerHelper);
    context.put("plugin", pluginDescriptor);
    context.put("pluginContext", getPluginContext());
    context.put("mojo", mojoExecution);
    context.put("basedir", basedir);
    context.put("project", project);
    context.put("properties", createProperties());
    context.put("session", session);
    context.put("settings", settings);
    context.put("ant", MagicContext.ANT_BUILDER);
    context.put("fail", new FailClosureTarget());

    return context;
  }
}
