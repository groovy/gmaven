package org.codehaus.gmaven.testsuite.suite;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Maven 3.0.5 and Groovy 2.0.6 combination test-suite.
 */
@RunWith(Suite.class)
public class Maven305_Groovy206_SuiteIT
    extends SuiteSupport
{
  @BeforeClass
  public static void configureCombination() {
    setMavenVersion("3.0.5");
    setGroovyVersion("2.0.6");
  }
}
