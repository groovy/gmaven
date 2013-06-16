/*
 * Copyright (c) 2007-2013, the original author or authors.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

package org.codehaus.gmaven.testsuite;

import java.io.File;
import java.net.URL;

import org.apache.maven.it.Verifier;
import org.junit.Test;

/**
 * Verify 'execute' basics.
 */
public class ExecuteBasicsXT
    extends ITSupport
{
  /**
   * Verifies all default context variables.
   */
  @Test
  public void defaultContext() throws Exception {
    Verifier verifier = executeScriptFileName("defaultContext.groovy");
    verifier.verifyErrorFreeLog();
    verifier.verifyTextInLog("ALL OK");
  }

  /**
   * Verify configured groovy runtime version matches what is detected.
   */
  @Test
  public void groovyVersion() throws Exception {
    Verifier verifier = executeScriptFileName("groovyVersion.groovy");
    verifier.verifyErrorFreeLog();
    verifier.verifyTextInLog("ALL OK");
    verifier.verifyTextInLog("Version: " + groovyVersion);
  }

  /**
   * Verify execution of a simple inline-source script.
   */
  @Test
  public void simpleInlineSource() throws Exception {
    Verifier verifier = executeScript("println(12345)");
    verifier.verifyErrorFreeLog();
    verifier.verifyTextInLog("12345");
  }

  /**
   * Verify execution of a simple file-source script.
   */
  @Test
  public void simpleFileSource() throws Exception {
    File file = new File(testIndex.getDirectory(), "simple.groovy");
    Verifier verifier = executeScript(file.getAbsolutePath());
    verifier.verifyErrorFreeLog();
    verifier.verifyTextInLog("ALL OK");
  }

  /**
   * Verify execution of a simple url-source script.
   */
  @Test
  public void simpleUrlSource() throws Exception {
    URL url = new File(testIndex.getDirectory(), "simple.groovy").toURI().toURL();
    Verifier verifier = executeScript(url.toExternalForm());
    verifier.verifyErrorFreeLog();
    verifier.verifyTextInLog("ALL OK");
  }
}
