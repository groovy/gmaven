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

package org.codehaus.gmaven.testsuite.tests;

import java.io.File;

import org.codehaus.gmaven.testsuite.ITSupport;
import org.junit.Test;

/**
 * Verify scriptpath resolution.
 */
public class Scriptpath
    extends ITSupport
{
  @Test
  public void scriptpathResolvesClass() throws Exception {
    verifier("scriptpath")
        .addProfile(testName.getMethodName())
        .setProperty("source", "hello.groovy")
        .executeGoal(goal("execute"))
        .errorFree()
        .logContains("Hello World");
  }

  @Test
  public void scriptpathPropertyResolvesClass() throws Exception {
    verifier("scriptpath")
        .setProperty("scriptpath", new File(testdir(), "scripts"))
        .setProperty("source", "hello.groovy")
        .executeGoal(goal("execute"))
        .errorFree()
        .logContains("Hello World");
  }

  @Test
  public void peerResolvesClass() throws Exception {
    verifier("scriptpath")
        .addProfile(testName.getMethodName())
        .setProperty("source", "scripts/goodbye.groovy")
        .executeGoal(goal("execute"))
        .errorFree()
        .logContains("Goodbye World");
  }
}
