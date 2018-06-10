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
package org.codehaus.gmaven.plugin;

import java.util.concurrent.atomic.AtomicInteger;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for {@link MojoSupport}.
 */
public class MojoSupportTest
    extends TestSupport
{
  private AtomicInteger prepareCount = new AtomicInteger(0);

  private AtomicInteger runCount = new AtomicInteger(0);

  private AtomicInteger cleanupCount = new AtomicInteger(0);

  private class MojoSupportTester
      extends MojoSupport
  {
    @Override
    protected void prepare() throws Exception {
      prepareCount.incrementAndGet();
    }

    @Override
    protected void run() throws Exception {
      runCount.incrementAndGet();
    }

    @Override
    protected void cleanup() throws Exception {
      cleanupCount.incrementAndGet();
    }
  }

  @Before
  public void setUp() throws Exception {
    prepareCount.set(0);
    runCount.set(0);
    cleanupCount.set(0);
  }

  @Test
  public void execute_prepare_run_cleanup() throws Exception {
    MojoSupport underTest = new MojoSupportTester();

    underTest.execute();

    assertThat(prepareCount.get(), is(1));
    assertThat(runCount.get(), is(1));
    assertThat(cleanupCount.get(), is(1));
  }

  @Test
  public void execute_prepareExceptionCallsCleanup() throws Exception {
    MojoSupport underTest = new MojoSupportTester()
    {
      @Override
      protected void prepare() throws Exception {
        throw new Exception("fail");
      }
    };

    try {
      underTest.execute();
      fail();
    }
    catch (Exception e) {
      // expected
    }

    assertThat(runCount.get(), is(0));
    assertThat(cleanupCount.get(), is(1));
  }

  @Test
  public void execute_runExceptionCallsCleanup() throws Exception {
    MojoSupport underTest = new MojoSupportTester()
    {
      @Override
      protected void run() throws Exception {
        throw new Exception("fail");
      }
    };

    try {
      underTest.execute();
      fail();
    }
    catch (Exception e) {
      // expected
    }

    assertThat(prepareCount.get(), is(1));
    assertThat(cleanupCount.get(), is(1));
  }

  @Test
  public void execute_MojoExecutionException_propagates() throws Exception {
    MojoSupport underTest = new MojoSupportTester()
    {
      @Override
      protected void run() throws Exception {
        throw new MojoExecutionException("fail");
      }
    };

    try {
      underTest.execute();
      fail();
    }
    catch (Exception e) {
      assertThat(e, instanceOf(MojoExecutionException.class));
    }
  }

  @Test
  public void execute_MojoFailureException_propagates() throws Exception {
    MojoSupport underTest = new MojoSupportTester()
    {
      @Override
      protected void run() throws Exception {
        throw new MojoFailureException("fail");
      }
    };

    try {
      underTest.execute();
      fail();
    }
    catch (Exception e) {
      assertThat(e, instanceOf(MojoFailureException.class));
    }
  }
}
