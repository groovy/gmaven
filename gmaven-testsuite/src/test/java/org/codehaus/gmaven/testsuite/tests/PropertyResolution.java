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

import org.codehaus.gmaven.testsuite.ITSupport;
import org.codehaus.gmaven.testsuite.MavenVerifier;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Verify property resolution.
 */
public class PropertyResolution
    extends ITSupport
{
  private MavenVerifier verifier() throws Exception {
    return verifier("property-resolution")
        .setProperty("source", "printResult.groovy")
        .addProfile(testName.getMethodName());
  }

  private void verify(final MavenVerifier verifier) throws Exception {
    verifier
        .errorFree()
        .logContains(
            "RESULT: OK",
            "ALL OK"
        );
  }

  private void verifyDefault() throws Exception {
    verify(
        verifier()
            .executeGoal(goal("execute"))
    );
  }

  @Test
  public void projectDefined() throws Exception {
    verifyDefault();
  }

  @Test
  public void defaultDefined() throws Exception {
    verifyDefault();
  }

  @Test
  public void defaultReferencesProjectProperty() throws Exception {
    verifyDefault();
  }

  @Test
  public void defaultReferencesSystemProperty() throws Exception {
    verify(
        verifier()
            .setProperty("foo", "OK")
            .executeGoal(goal("execute"))
    );
  }

  @Test
  public void overrideDefined() throws Exception {
    verifyDefault();
  }

  @Test
  public void overrideReferencesSystemProperty() throws Exception {
    verify(
        verifier()
            .setProperty("foo", "OK")
            .executeGoal(goal("execute"))
    );
  }

  @Test
  @Ignore("FIXME: This test has issues due to Maven interpolation before execution")
  public void overrideReferencesDefault() throws Exception {
    verifyDefault();
  }
}
