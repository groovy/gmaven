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

import org.apache.maven.it.Verifier;

/**
 * Wrapper around {@link org.apache.maven.it.Verifier} to provide saner API.
 */
public class MavenVerifier
{
  private final Verifier delegate;

  public MavenVerifier(final File projectDir, final File settingsFile) throws Exception {
    this.delegate = new Verifier(projectDir.getAbsolutePath(), settingsFile.getAbsolutePath());
  }

  public MavenVerifier setProperty(final String name, final String value) {
    delegate.getSystemProperties().setProperty(name, value);
    return this;
  }

  public MavenVerifier addArg(final String arg) {
    delegate.addCliOption(arg);
    return this;
  }

  public MavenVerifier addProfile(final String profileId) {
    delegate.addCliOption("-P" + profileId);
    return this;
  }

  public MavenVerifier errorFree() throws Exception {
    delegate.verifyErrorFreeLog();
    return this;
  }

  public MavenVerifier logContains(final String... values) throws Exception {
    for (String value : values) {
      delegate.verifyTextInLog(value);
    }
    return this;
  }

  public MavenVerifier executeGoal(final String goal) throws Exception {
    delegate.executeGoal(goal);
    return this;
  }
}
