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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.maven.it.Verifier;
import org.junit.Before;
import org.junit.Test;

/**
 * ???
 */
public class VerifierXIT
    extends TestSupport
{
  private String mavenVersion;

  private String groovyVersion;

  @Before
  public void setUp() throws Exception {
    mavenVersion = System.getProperty("maven.version", "3.0.5");
    groovyVersion = System.getProperty("groovy.version", "2.1.5");
  }

  private Verifier createVerifier(final String projectName) throws Exception {
    System.setProperty("verifier.forkMode", "embedded");
    System.setProperty("maven.home", util.resolvePath("target/filesets/apache-maven-" + mavenVersion));

    File projectDir = prepareProjectDir(projectName);
    log("Project dir: {}", projectDir);

    File settingsFile = util.resolveFile("target/test-classes/settings.xml");
    log("Settings file: {}", settingsFile);

    Verifier verifier = new Verifier(
        projectDir.getAbsolutePath(),
        settingsFile.getAbsolutePath()
    );

    // this can be pretty slow, also unless we install the plugin we built the deployed version will be used
    //File localRepo = util.resolveFile("target/maven-localrepo");
    //log("Local repo: {}", localRepo);
    //verifier.setLocalRepo(localRepo.getAbsolutePath());

    return verifier;
  }

  private File prepareProjectDir(final String projectName) throws IOException {
    File sourceDir = util.resolveFile("src/test/it-projects/" + projectName);
    log("Source directory: {}", sourceDir);

    File targetDir = util.resolveFile("target/it-projects/" + sourceDir.getName());
    log("Target directory: {}", targetDir);

    log("Copying {} -> {}", sourceDir, targetDir);
    FileUtils.deleteDirectory(targetDir);
    FileUtils.copyDirectory(sourceDir, targetDir);

    return targetDir;
  }

  @Test
  public void sysProps() throws Exception {
    Map<String,String> props = Maps.fromProperties(System.getProperties());
    List<String> keys = Lists.newArrayList(props.keySet());
    Collections.sort(keys);
    for (String key : keys) {
      log("{}={}", key, props.get(key));
    }
  }

  @Test
  public void execute_verifyGroovyVersion() throws Exception {
    Verifier verifier = createVerifier("execute-script");

    String testName = "groovyVersion";
    verifier.getSystemProperties().setProperty("testName", testName);
    verifier.getSystemProperties().setProperty("source", "${project.basedir}/${testName}.groovy");
    verifier.setLogFileName(testName + "-test.log");

    verifier.getSystemProperties().setProperty("groovy.version", groovyVersion);
    verifier.executeGoal("groovy:execute");
    verifier.verifyErrorFreeLog();
    verifier.verifyTextInLog("ALL OK");
    verifier.verifyTextInLog("Version: " + groovyVersion);

    verifier.resetStreams();
  }

}
