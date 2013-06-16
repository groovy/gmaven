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

package org.codehaus.gmaven.testsuite.xt;

import org.codehaus.gmaven.testsuite.ITSupport;
import org.codehaus.gmaven.testsuite.MavenVerifier;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Verify property resolution.
 */
public class PropertyResolutionXT
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

  @Test
  public void onlyDefaultDefined() throws Exception {
    verify(
        verifier()
            .executeGoal(goal("execute"))
    );
  }

  @Test
  public void defaultReferencesProjectProperty() throws Exception {
    verify(
        verifier()
            .executeGoal(goal("execute"))
    );
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
    verify(
        verifier()
            .executeGoal(goal("execute"))
    );
  }
}
