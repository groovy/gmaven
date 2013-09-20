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

package org.codehaus.gmaven.testsuite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.sonatype.sisu.litmus.testsupport.TestSupport;
import org.sonatype.sisu.litmus.testsupport.hamcrest.FileMatchers;
import org.sonatype.sisu.litmus.testsupport.junit.TestIndexRule;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import static org.junit.Assert.assertThat;

/**
 * Support for integration tests.
 */
public abstract class ITSupport
    extends TestSupport
{
  public static final String DEFAULT_MAVEN_VERSION = "3.0.5";

  public static final String DEFAULT_GROOVY_VERSION = "2.1.5";

  public static final String DEFAULT_UNDER_TEST_VERSION = "2.1-SNAPSHOT";

  /**
   * The version of the groovy-maven-plugin which is under test.
   */
  private String underTestVersion;

  private String mavenVersion;

  private File mavenHome;

  private File buildRepository;

  private File settingsFile;

  private File localRepository;

  private String groovyVersion;

  @Rule
  public final TestName testName = new TestName();

  @Rule
  public final TestIndexRule testIndex = new TestIndexRule(
      util.resolveFile("target/it-reports"),
      util.resolveFile("target/it-work")
  );

  /**
   * Returns the configured Maven version.
   */
  protected String getMavenVersion() {
    return mavenVersion;
  }

  /**
   * Returns the configured Groovy version.
   */
  protected String getGroovyVersion() {
    return groovyVersion;
  }

  @Before
  public void setUp() throws Exception {
    log("Index: {}", testIndex.getDirectory().getName());

    underTestVersion = detectUnderTestVersion();
    log("Under-test version: {}", underTestVersion);

    log("System properties:");
    logProperties(System.getProperties());

    mavenVersion = System.getProperty("maven.version", DEFAULT_MAVEN_VERSION);
    log("Maven version: {}", mavenVersion);
    mavenHome = util.resolveFile("target/filesets/apache-maven-" + mavenVersion);
    assertThat("Missing maven installation: " + mavenHome,
        mavenHome, FileMatchers.exists());
    System.setProperty("maven.home", mavenHome.getAbsolutePath());
    log("Maven home: {}", mavenHome);

    buildRepository = detectBuildRepository();
    log("Build repository: {}", buildRepository);

    settingsFile = createSettingsFile(buildRepository);
    log("Settings file: {}", settingsFile);

    // FIXME: Setting local repo here isn't working properly with settings. Just use the default for now.
    //localRepository = util.resolveFile("target/maven-localrepo");
    //log("Local repository: {}", localRepository);

    groovyVersion = System.getProperty("groovy.version", DEFAULT_GROOVY_VERSION);
    log("Groovy version: {}", groovyVersion);

    //System.setProperty("verifier.forkMode", "embedded");
    System.setProperty("verifier.forkMode", "fork");

    testIndex.recordInfo("maven", mavenVersion);
    testIndex.recordInfo("groovy", groovyVersion);

    System.out.println(">>>>>>>>>>");
  }

  @After
  public void tearDown() throws Exception {
    System.out.println("<<<<<<<<<<");

    // FIXME: record failsafe details; this won't work because suite class is used
    //String failsafePrefix = "../../failsafe-reports/" + getClass().getName();
    //reportFile("test summary", failsafePrefix + ".txt");
    //reportFile("test output", failsafePrefix + "-output.txt");

    // record build log in index report
    reportFile("mvn log", "log.txt");
  }

  /**
   * Return the directory where tests are run from.
   */
  protected File testdir() {
    return testIndex.getDirectory();
  }

  /**
   * Log a properties object.
   */
  private void logProperties(final Properties source) {
    Map<String, String> map = Maps.fromProperties(source);
    List<String> keys = Lists.newArrayList(map.keySet());
    Collections.sort(keys);
    for (String key : keys) {
      log("  {}={}", key, map.get(key));
    }
  }

  /**
   * Load a resources as a {@link Properties}.
   */
  private Properties loadResourceProperties(final String resourceName) throws IOException {
    Properties props = new Properties();

    URL resource = getClass().getResource(resourceName);
    if (resource != null) {
      log("Loading properties from: {}", resource);
      InputStream stream = resource.openStream();
      try {
        props.load(stream);
      }
      finally {
        stream.close();
      }
    }

    return props;
  }

  // FIXME: Look into why this might not be working...

  /**
   * Try to figure out what the version of the plugin we are testing.  Falls back to {@link #DEFAULT_UNDER_TEST_VERSION}.
   */
  private String detectUnderTestVersion() throws IOException {
    Properties props = loadResourceProperties("/META-INF/maven/org.codehaus.gmaven/groovy-maven-plugin/pom.properties");
    String version = props.getProperty("version");

    // this can happen in IDE context, complain and move on
    if (version == null) {
      version = DEFAULT_UNDER_TEST_VERSION;
      log("WARNING: unable to detect under-test version!");
    }

    return version;
  }

  /**
   * Create the settings file which will be used for verification builds.
   */
  private File createSettingsFile(final File buildRepository) throws Exception {
    return interpolate(
        util.resolveFile("src/test/it-projects/settings.xml"),
        ImmutableMap.of(
            "localRepositoryUrl", buildRepository.toURI().toURL().toExternalForm()
        )
    );
  }

  /**
   * Create a new file interpolating from given template.
   */
  private File interpolate(final File template, final Map<String, String> context) throws Exception {
    String content = FileUtils.readFileToString(template);
    Interpolator interpolator = new StringSearchInterpolator();
    interpolator.addValueSource(new MapBasedValueSource(context));
    content = interpolator.interpolate(content);
    File file = util.createTempFile(template.getName());
    FileUtils.write(file, content);
    return file;
  }

  /**
   * Detect the repository used to build the plugin under test.
   */
  private File detectBuildRepository() {
    String path = System.getProperty("buildRepository");
    if (path != null) {
      return new File(path).getAbsoluteFile();
    }

    // fall back to default
    File userHome = new File(System.getProperty("user.home"));
    return new File(userHome, ".m2/repository");
  }

  /**
   * Record a report file relative to the work dir in the index.
   */
  private void reportFile(final String label, final String fileName) {
    testIndex.recordAndCopyLink(label, new File(testdir(), fileName));
  }

  /**
   * Create a verifier for the given project-name, cloning its contents to test work directory.
   */
  protected MavenVerifier verifier(final String projectName) throws Exception {
    File sourceDir = util.resolveFile("src/test/it-projects/" + projectName);
    File projectDir = testdir();

    log("Copying {} -> {}", sourceDir, projectDir);
    FileUtils.copyDirectory(sourceDir, projectDir);

    return new MavenVerifier(projectDir, settingsFile)
        .addArg("-V")
        .setProperty("underTest.version", underTestVersion)
        .setProperty("groovy.version", groovyVersion)
        .setProperty("gmaven.logging", "DEBUG");
  }

  /**
   * Construct fully qualified goal name for plugin under test.
   */
  protected String goal(final String execute) {
    return String.format("org.codehaus.gmaven:groovy-maven-plugin:%s:%s", underTestVersion, execute);
  }
}
