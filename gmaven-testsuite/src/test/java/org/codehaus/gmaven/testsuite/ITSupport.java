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
import java.util.Properties;

import org.sonatype.sisu.litmus.testsupport.TestSupport;
import org.sonatype.sisu.litmus.testsupport.hamcrest.FileMatchers;
import org.sonatype.sisu.litmus.testsupport.junit.TestIndexRule;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.Verifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import static org.junit.Assert.assertThat;

/**
 * Support for integration tests.
 */
public class ITSupport
    extends TestSupport
{
  public static final String DEFAULT_MAVEN_VERSION = "3.0.5";

  public static final String DEFAULT_GROOVY_VERSION = "2.1.5";

  protected String mavenVersion;

  protected File mavenHome;

  protected String groovyVersion;

  @Rule
  public final TestName testName = new TestName();

  @Rule
  public final TestIndexRule testIndex = new TestIndexRule(util.resolveFile("target/it-work"));

  @Before
  public void setUp() throws Exception {
    //Map<String, String> props = Maps.fromProperties(System.getProperties());
    //List<String> keys = Lists.newArrayList(props.keySet());
    //Collections.sort(keys);
    //log("System properties:");
    //for (String key : keys) {
    //  log("  {}={}", key, props.get(key));
    //}

    mavenVersion = System.getProperty("maven.version", DEFAULT_MAVEN_VERSION);
    mavenHome = util.resolveFile("target/filesets/apache-maven-" + mavenVersion);
    assertThat(mavenHome, FileMatchers.exists());
    System.setProperty("maven.home", mavenHome.getAbsolutePath());

    groovyVersion = System.getProperty("groovy.version", DEFAULT_GROOVY_VERSION);

    //System.setProperty("verifier.forkMode", "embedded");
    System.setProperty("verifier.forkMode", "fork");

    testIndex.recordInfo("maven", mavenVersion);
    testIndex.recordInfo("groovy", groovyVersion);
  }

  private void recordLink(final String label, final String fileName) {
    testIndex.recordLink(label, new File(testIndex.getDirectory(), fileName));
  }

  @After
  public void tearDown() throws Exception {
    recordLink("mvn log", "log.txt");
  }

  protected Verifier createVerifier(final String projectName) throws Exception {
    File sourceDir = util.resolveFile("src/test/it-projects/" + projectName);
    File projectDir = testIndex.getDirectory();

    log("Copying {} -> {}", sourceDir, projectDir);
    FileUtils.copyDirectory(sourceDir, projectDir);

    Verifier verifier = new Verifier(projectDir.getAbsolutePath());

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
