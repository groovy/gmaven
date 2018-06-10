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
package org.codehaus.gmaven.testsuite.tests;

import java.io.File;
import java.net.URL;

import org.codehaus.gmaven.testsuite.ITSupport;
import org.codehaus.gmaven.testsuite.MavenVerifier;
import org.junit.Test;

/**
 * Verify 'execute' basics.
 */
public class ExecuteBasics
    extends ITSupport
{
  private MavenVerifier executeScript(final String source) throws Exception {
    return verifier("execute-script")
        .setProperty("source", source)
        .executeGoal(goal("execute"));
  }

  private MavenVerifier executeScriptFileName(final String fileName) throws Exception {
    return executeScript(new File(testIndex.getDirectory(), fileName).getAbsolutePath());
  }

  /**
   * Verifies all default context variables.
   */
  @Test
  public void defaultContext() throws Exception {
    executeScriptFileName("defaultContext.groovy")
        .errorFree()
        .logContains("ALL OK");
  }

  /**
   * Verify configured groovy runtime version matches what is detected.
   */
  @Test
  public void groovyVersion() throws Exception {
    executeScriptFileName("groovyVersion.groovy")
        .errorFree()
        .logContains(
            "Version: " + getGroovyVersion(),
            "ALL OK"
        );
  }

  /**
   * Verify execution of a simple inline-source script.
   */
  @Test
  public void simpleInlineSource() throws Exception {
    executeScript("println(12345+1)")
        .errorFree()
        .logContains("12346");
  }

  /**
   * Verify execution of a simple file-source script.
   */
  @Test
  public void simpleFileSource() throws Exception {
    File file = new File(testIndex.getDirectory(), "simple.groovy");
    executeScript(file.getAbsolutePath())
        .errorFree()
        .logContains("ALL OK");
  }

  /**
   * Verify execution of a simple url-source script.
   */
  @Test
  public void simpleUrlSource() throws Exception {
    URL url = new File(testIndex.getDirectory(), "simple.groovy").toURI().toURL();
    executeScript(url.toExternalForm())
        .errorFree()
        .logContains("ALL OK");
  }
}
