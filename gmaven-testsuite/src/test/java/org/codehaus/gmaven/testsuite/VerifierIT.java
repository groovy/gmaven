package org.codehaus.gmaven.testsuite;

import java.io.File;
import java.io.IOException;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.apache.commons.io.FileUtils;
import org.apache.maven.it.DefaultVerifier;
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

    Verifier verifier = new DefaultVerifier(
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
