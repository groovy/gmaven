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

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link FailClosureTarget}.
 */
public class FailClosureTargetTest
    extends TestSupport
{
  private FailClosureTarget underTest;

  @Before
  public void setUp() throws Exception {
    underTest = new FailClosureTarget();
  }

  @Test
  public void fail_null() throws Exception {
    try {
      underTest.call(null);
    }
    catch (MojoExecutionException e) {
      assertThat(e.getMessage(), is(FailClosureTarget.FAILED));
    }
  }

  @Test
  public void fail_noArgs() throws Exception {
    try {
      underTest.call(new Object[0]);
    }
    catch (MojoExecutionException e) {
      assertThat(e.getMessage(), is(FailClosureTarget.FAILED));
    }
  }

  @Test
  public void fail_object() throws Exception {
    try {
      underTest.call(new Object[]{"foo"});
    }
    catch (MojoExecutionException e) {
      assertThat(e.getMessage(), is("foo"));
      assertThat(e.getCause(), nullValue());
    }
  }

  @Test
  public void fail_throwable() throws Exception {
    @SuppressWarnings("ThrowableInstanceNeverThrown")
    Throwable cause = new Throwable("foo");
    try {
      underTest.call(new Object[]{cause});
    }
    catch (MojoExecutionException e) {
      assertThat(e.getMessage(), is("foo"));
      assertThat(e.getCause(), is(cause));
    }
  }

  @Test
  public void fail_objectAndThrowable() throws Exception {
    @SuppressWarnings("ThrowableInstanceNeverThrown")
    Throwable cause = new Throwable("bar");
    try {
      underTest.call(new Object[]{"foo", cause});
    }
    catch (MojoExecutionException e) {
      assertThat(e.getMessage(), is("foo"));
      assertThat(e.getCause(), is(cause));
    }
  }

  /**
   * 2-arg form, 2nd arg must be a Throwable.
   */
  @Test(expected = Error.class)
  public void fail_invalidArgs() throws Exception {
    underTest.call(new Object[]{1, 2});
  }

  /**
   * more than 2 args is not supported.
   */
  @Test(expected = Error.class)
  public void fail_tooManyArgs() throws Exception {
    underTest.call(new Object[]{1, 2, 3});
  }
}
