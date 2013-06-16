package org.codehaus.gmaven.testsuite.suite;

import org.codehaus.gmaven.testsuite.ExecuteBasicsXT;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Maven 3.1.x and Groovy 2.1.5 combination test-suite.
 */
@RunWith(Suite.class)
@SuiteClasses(
    ExecuteBasicsXT.class
)
public class Maven31x_Groovy215_SuiteIT
    extends SuiteSupport
{
  @BeforeClass
  public static void configureCombination() {
    setMavenVersion("3.1.0-alpha-1");
    setGroovyVersion("2.1.5");
  }
}