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

package org.codehaus.gmaven.plugin.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.codehaus.gmaven.plugin.util.SystemNoExitGuard.Task;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link SystemNoExitGuard}.
 */
public class SystemNoExitGuardTest
    extends TestSupport
{
  @Test
  public void systemStreamsAreRestored() throws Exception {
    InputStream systemIn = System.in;
    PrintStream systemOut = System.out;
    PrintStream systemErr = System.err;

    new SystemNoExitGuard().run(new Task()
    {
      @Override
      public void run() throws Exception {
        // change all system streams
        System.setIn(new BufferedInputStream(System.in));
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
      }
    });

    // ensure that all system streams are reset
    assertThat(System.in, is(systemIn));
    assertThat(System.out, is(systemOut));
    assertThat(System.err, is(systemErr));
  }
}
