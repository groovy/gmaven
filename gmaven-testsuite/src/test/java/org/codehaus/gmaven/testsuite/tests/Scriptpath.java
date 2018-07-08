/*
 * Copyright (c) 2006-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.gmaven.testsuite.tests;

import java.io.File;

import org.codehaus.gmaven.testsuite.ITSupport;
import org.codehaus.gmaven.testsuite.MavenVerifier;
import org.junit.Test;

/**
 * Verify scriptpath resolution.
 */
public class Scriptpath
    extends ITSupport
{
  private MavenVerifier verifier() throws Exception {
    return verifier("scriptpath")
        .addProfile(testName.getMethodName());
  }

  @Test
  public void scriptpathResolvesClass() throws Exception {
    verifier()
        .setProperty("source", "hello.groovy")
        .executeGoal(goal("execute"))
        .errorFree()
        .logContains("Hello World");
  }

  @Test
  public void scriptpathPropertyResolvesClass() throws Exception {
    verifier()
        .setProperty("scriptpath", new File(testdir(), "scripts"))
        .setProperty("source", "hello.groovy")
        .executeGoal(goal("execute"))
        .errorFree()
        .logContains("Hello World");
  }

  @Test
  public void peerResolvesClass() throws Exception {
    verifier()
        .setProperty("source", "scripts/goodbye.groovy")
        .executeGoal(goal("execute"))
        .errorFree()
        .logContains("Goodbye World");
  }
}
