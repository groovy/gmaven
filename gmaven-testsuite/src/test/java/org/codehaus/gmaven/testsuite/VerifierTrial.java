package org.codehaus.gmaven.testsuite;

import java.io.File;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.apache.maven.it.DefaultVerifier;
import org.apache.maven.it.Verifier;
import org.junit.Test;

/**
 * ???
 */
public class VerifierTrial
  extends TestSupport
{
  @Test
  public void test() throws Exception {
    System.setProperty("maven.home", util.resolvePath("target/filesets/apache-maven-3.0.5"));

    Verifier verifier = new DefaultVerifier(util.resolvePath("src/test/it-projects/with-classes"));
    verifier.setForkJvm(true);

    File localRepo = util.resolveFile("target/maven-localrepo");
    verifier.setLocalRepo(localRepo.getAbsolutePath());

    verifier.executeGoal("verify");
  }
}
