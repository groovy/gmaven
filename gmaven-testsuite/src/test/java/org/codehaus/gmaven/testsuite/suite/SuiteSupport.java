/*
 * Copyright (c) 2006-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
