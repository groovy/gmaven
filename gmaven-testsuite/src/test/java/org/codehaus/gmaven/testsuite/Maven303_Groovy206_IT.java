package org.codehaus.gmaven.testsuite;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * ???
 */
@RunWith(Suite.class)
@SuiteClasses(
    VerifierTrial.class
)
public class Maven303_Groovy206_IT
{
  @BeforeClass
  public static void configureTestsuite() {
    System.setProperty("maven.version", "3.0.3");
    System.setProperty("groovy.version", "2.0.6");
  }
}
