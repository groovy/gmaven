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
 * Extended {@link org.apache.maven.it.Verifier}.
 */
public class MavenVerifier
  extends org.apache.maven.it.Verifier
{
  public MavenVerifier(final File projectDir, final File settingsFile) throws Exception {
    super(projectDir.getAbsolutePath(), settingsFile.getAbsolutePath());
  }

  public MavenVerifier errorFree() throws Exception {
    verifyErrorFreeLog();
    return this;
  }

  public MavenVerifier logContains(final String... values) throws Exception {
    for (String value : values) {
      verifyTextInLog(value);
    }
    return this;
  }
}
