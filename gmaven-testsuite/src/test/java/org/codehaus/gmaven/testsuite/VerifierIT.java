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

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.Verifier;
import org.junit.Test;

/**
 * ???
 */
public class VerifierIT
    extends TestSupport
{
  @Test
  public void test() throws Exception {
    System.setProperty("verifier.forkMode", "embedded");
    System.setProperty("maven.home", util.resolvePath("target/filesets/apache-maven-3.0.5"));

    File projectDir = prepareProjectDir("with-classes");
    File localRepo = util.resolveFile("target/maven-localrepo");

    Verifier verifier = new Verifier(
        projectDir.getAbsolutePath(),
        null, // settings
        true, // debug
        false // fork
    );
    verifier.setLocalRepo(localRepo.getAbsolutePath());
    //verifier.resetStreams();

    log("Executing goals");
    verifier.executeGoal("verify");

    log("Verifying results");
    verifier.verifyErrorFreeLog();
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
}
