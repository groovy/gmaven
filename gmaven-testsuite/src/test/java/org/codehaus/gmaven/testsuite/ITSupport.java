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
import org.apache.maven.it.Verifier;
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

  protected String mavenVersion;

  protected File mavenHome;

  protected File buildRepository;

  protected File settingsFile;

  protected File localRepository;

  protected String groovyVersion;

  @Rule
  public final TestName testName = new TestName();

  @Rule
  public final TestIndexRule testIndex = new TestIndexRule(util.resolveFile("target/it-work"));

  @Before
  public void setUp() throws Exception {
    logSystemProperties();

    mavenVersion = System.getProperty("maven.version", DEFAULT_MAVEN_VERSION);
    log("Maven version: {}", mavenVersion);
    mavenHome = util.resolveFile("target/filesets/apache-maven-" + mavenVersion);
    assertThat(mavenHome, FileMatchers.exists());
    System.setProperty("maven.home", mavenHome.getAbsolutePath());
    log("Maven home: {}", mavenHome);

    buildRepository = detectBuildRepository();
    log("Build repository: {}", buildRepository);

    settingsFile = createSettingsFile(buildRepository);
    log("Settings file: {}", settingsFile);

    localRepository = util.resolveFile("target/maven-localrepo");
    log("Local repository: {}", localRepository);

    groovyVersion = System.getProperty("groovy.version", DEFAULT_GROOVY_VERSION);
    log("Groovy version: {}", groovyVersion);

    //System.setProperty("verifier.forkMode", "embedded");
    System.setProperty("verifier.forkMode", "fork");

    testIndex.recordInfo("maven", mavenVersion);
    testIndex.recordInfo("groovy", groovyVersion);

    System.out.println(">>>>>>>>>>");
  }

  private void logSystemProperties() {
    Map<String, String> props = Maps.fromProperties(System.getProperties());
    List<String> keys = Lists.newArrayList(props.keySet());
    Collections.sort(keys);
    log("System properties:");
    for (String key : keys) {
      log("  {}={}", key, props.get(key));
    }
  }

  private File createSettingsFile(final File buildRepository) throws Exception {
    return interpolate(
        util.resolveFile("src/test/it-projects/settings.xml"),
        ImmutableMap.of(
            "localRepositoryUrl", buildRepository.toURI().toURL().toExternalForm()
        )
    );
  }

  private File interpolate(final File template, final Map<String, String> context) throws Exception {
    String content = FileUtils.readFileToString(template);
    Interpolator interpolator = new StringSearchInterpolator();
    interpolator.addValueSource(new MapBasedValueSource(context));
    content = interpolator.interpolate(content);
    File file = util.createTempFile();
    FileUtils.write(file, content);
    return file;
  }

  private File detectBuildRepository() {
    String path = System.getProperty("buildRepository");
    if (path != null) {
      return new File(path).getAbsoluteFile();
    }

    // fall back to default
    File userHome = new File(System.getProperty("user.home"));
    return new File(userHome, ".m2/repository");
  }

  protected void recordLink(final String label, final String fileName) {
    testIndex.recordLink(label, new File(testIndex.getDirectory(), fileName));
  }

  @After
  public void tearDown() throws Exception {
    System.out.println("<<<<<<<<<<");

    // record build log in index report
    recordLink("mvn log", "log.txt");
  }

  protected Verifier createVerifier(final String projectName) throws Exception {
    File sourceDir = util.resolveFile("src/test/it-projects/" + projectName);
    File projectDir = testIndex.getDirectory();

    log("Copying {} -> {}", sourceDir, projectDir);
    FileUtils.copyDirectory(sourceDir, projectDir);

    Verifier verifier = new Verifier(
        projectDir.getAbsolutePath(),
        settingsFile.getAbsolutePath()
    );

    verifier.addCliOption("-V");

    // this can be pretty slow, also unless we install the plugin we built the deployed version will be used
    //File localRepo = util.resolveFile("target/maven-localrepo");
    //log("Local repo: {}", localRepo);
    //verifier.setLocalRepo(localRepo.getAbsolutePath());

    return verifier;
  }

  protected Verifier executeScript(final String source) throws Exception {
    log("Execute script: {}", source);

    Verifier verifier = createVerifier("execute-script");

    Properties sysprops = verifier.getSystemProperties();
    sysprops.setProperty("groovy.version", groovyVersion);
    sysprops.setProperty("source", source);

    verifier.executeGoal("org.codehaus.gmaven:groovy-maven-plugin:execute");
    verifier.resetStreams();

    return verifier;
  }

  protected Verifier executeScriptFileName(final String fileName) throws Exception {
    return executeScript(new File(testIndex.getDirectory(), fileName).getAbsolutePath());
  }
}
