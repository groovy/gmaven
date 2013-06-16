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

/**
 * Helper to build a {@link MavenVerifier} instance.
 */
public class MavenVerifierBuilder
{
  private File projectDir;

  private File settingsFile;

  private MavenVerifier verfier;

  public MavenVerifierBuilder(final File projectDir, final File settingsFile) throws Exception {
    this.projectDir = projectDir;
    this.settingsFile = settingsFile;

    verfier = new MavenVerifier(projectDir, settingsFile);
    verfier.resetStreams();
  }

  public File getProjectDir() {
    return projectDir;
  }

  public File getSettingsFile() {
    return settingsFile;
  }

  public MavenVerifier getVerfier() {
    return verfier;
  }

  public MavenVerifierBuilder setProperty(final String name, final String value) {
    verfier.getSystemProperties().setProperty(name, value);
    return this;
  }

  public MavenVerifierBuilder addArg(final String arg) {
    verfier.addCliOption(arg);
    return this;
  }

  public MavenVerifierBuilder addProfile(final String profileId) {
    verfier.addCliOption("-P" + profileId);
    return this;
  }

  public MavenVerifier build() {
    return verfier;
  }
}
