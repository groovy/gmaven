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
 * Verify property defaults.
 */
public class PropertyDefaultsXT
    extends ITSupport
{
  @Test
  public void verify_1() throws Exception {
    Verifier verifier = createVerifier("property-defaults");
    verifier.getSystemProperties().put("source", "verify.groovy");
    verifier.addCliOption("-P1");
    verifier.executeGoal(goal("execute"));
    verifier.verifyErrorFreeLog();
    verifier.verifyTextInLog("ALL OK");
  }
}
