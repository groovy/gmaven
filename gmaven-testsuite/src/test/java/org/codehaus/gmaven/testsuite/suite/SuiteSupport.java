package org.codehaus.gmaven.testsuite.suite;

/**
 * Support for suites.
 */
public class SuiteSupport
{
  protected static void setMavenVersion(final String version) {
    System.setProperty("maven.version", version);
  }

  protected static void setGroovyVersion(final String version) {
    System.setProperty("groovy.version", version);
  }
}
