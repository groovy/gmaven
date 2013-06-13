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
package org.codehaus.gmaven.plugin;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

import static org.codehaus.gmaven.plugin.ClasspathScope.compile;
import static org.codehaus.gmaven.plugin.ClasspathScope.none;
import static org.codehaus.gmaven.plugin.ClasspathScope.provided;
import static org.codehaus.gmaven.plugin.ClasspathScope.runtime;
import static org.codehaus.gmaven.plugin.ClasspathScope.test;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ClasspathScope}.
 */
public class ClasspathScopeTest
    extends TestSupport
{
  @Test
  public void matches_none() {
    ClasspathScope value = none;
    assertThat(value.matches(none), is(true));
    assertThat(value.matches(provided), is(false));
    assertThat(value.matches(compile), is(false));
    assertThat(value.matches(runtime), is(false));
    assertThat(value.matches(test), is(false));
  }

  @Test
  public void matches_provided() {
    ClasspathScope value = provided;
    assertThat(value.matches(none), is(false));
    assertThat(value.matches(provided), is(true));
    assertThat(value.matches(compile), is(false));
    assertThat(value.matches(runtime), is(false));
    assertThat(value.matches(test), is(false));
  }

  @Test
  public void matches_compile() {
    ClasspathScope value = compile;
    assertThat(value.matches(none), is(false));
    assertThat(value.matches(provided), is(true));
    assertThat(value.matches(compile), is(true));
    assertThat(value.matches(runtime), is(false));
    assertThat(value.matches(test), is(false));
  }

  @Test
  public void matches_runtime() {
    ClasspathScope value = runtime;
    assertThat(value.matches(none), is(false));
    assertThat(value.matches(provided), is(true));
    assertThat(value.matches(compile), is(true));
    assertThat(value.matches(runtime), is(true));
    assertThat(value.matches(test), is(false));
  }

  @Test
  public void matches_test() {
    ClasspathScope value = test;
    assertThat(value.matches(none), is(false));
    assertThat(value.matches(provided), is(true));
    assertThat(value.matches(compile), is(true));
    assertThat(value.matches(runtime), is(true));
    assertThat(value.matches(test), is(true));
  }
}
