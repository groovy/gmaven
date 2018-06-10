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
package org.codehaus.gmaven.plugin.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.codehaus.gmaven.plugin.ExecuteMojo;
import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.codehaus.gmaven.plugin.util.SystemNoExitGuard.Task;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
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

  /**
   * If maven is running in multi-threaded environment then
   * SystemNoExitGuard breaks build because there is no
   * guarantee that the first started groovy task will
   * finish last.
   * Imagine we have 2 groovy tasks - task1, task2. And they are executed in the
   * following order:
   *
   * task1.start
   * task2.start
   * task1.finish
   * task2.finish
   *
   * In that case task2.start will use task1.securityManager
   * and that security manager will be applied after task2.finish
   */
  @Test
  public void testTaskOverlapping() throws Exception {
    final SecurityManager sm = System.getSecurityManager();

    final CyclicBarrier barrier1 = new CyclicBarrier(2);
    final CyclicBarrier barrier2 = new CyclicBarrier(2);
    final List<Exception> exceptions = new CopyOnWriteArrayList<Exception>();

    // start task1
    Thread thread1 = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          new SystemNoExitGuard().run(new Task() {
            @Override
            public void run() throws Exception {
              barrier1.await();
              // wait task2 to start and finish execution
              barrier1.await();
            }
          });
        } catch (Exception e) {
          exceptions.add(e);
        }
      }
    });
    thread1.start();

    // wait task1 to start
    barrier1.await();

    // And start task 2
    Thread thread2 = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          new SystemNoExitGuard().run(new Task() {
            @Override
            public void run() throws Exception {
              // wait for task start
              barrier2.await();
              // wait for task1 to finish and exit
              barrier2.await();
            }
          });
        } catch (Exception e) {
          exceptions.add(e);
        }
      }
    });
    thread2.start();

    // wait task2 to start
    barrier2.await();

    // wait task1 to finish
    barrier1.await();
    thread1.join();

    // unlock task2 and wait it to finish
    barrier2.await();
    thread2.join();

    assertEquals(sm, System.getSecurityManager());
  }
}
