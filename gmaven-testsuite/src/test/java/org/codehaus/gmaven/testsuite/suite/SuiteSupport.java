package org.codehaus.gmaven.testsuite.suite;

import org.codehaus.gmaven.testsuite.tests.ExecuteBasics;
import org.codehaus.gmaven.testsuite.tests.PropertyResolution;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Support for suites.
 */
@SuiteClasses({
    ExecuteBasics.class,
    PropertyResolution.class
})
public abstract class SuiteSupport
{
  protected static void setMavenVersion(final String version) {
    System.setProperty("maven.version", version);
  }

  protected static void setGroovyVersion(final String version) {
    System.setProperty("groovy.version", version);
  }
}
