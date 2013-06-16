package org.codehaus.gmaven.testsuite.suite;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Maven 3.0.5 and Groovy 2.1.5 combination test-suite.
 */
@RunWith(Suite.class)
public class Maven305_Groovy215_SuiteIT
    extends SuiteSupport
{
  @BeforeClass
  public static void configureCombination() {
    setMavenVersion("3.0.5");
    setGroovyVersion("2.1.5");
  }
}
