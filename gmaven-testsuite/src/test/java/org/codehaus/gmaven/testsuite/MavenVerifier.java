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

  public MavenVerifier setProperty(final String name, final Object value) {
    delegate.getSystemProperties().setProperty(name, String.valueOf(value));
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
