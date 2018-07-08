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

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link NoExitSecurityManager}.
 */
public class NoExitSecurityManagerTest
    extends TestSupport
{
  private SecurityManager original;

  @Before
  public void setUp() throws Exception {
    original = System.getSecurityManager();
    System.setSecurityManager(new NoExitSecurityManager(original));
  }

  @After
  public void tearDown() throws Exception {
    System.setSecurityManager(original);
    original = null;
  }

  @Test(expected = SecurityException.class)
  public void system_exit() {
    System.exit(0);
  }

  @Test(expected = SecurityException.class)
  public void runtime_exit() {
    Runtime.getRuntime().exit(0);
  }

  @Test(expected = SecurityException.class)
  public void runtime_halt() {
    Runtime.getRuntime().halt(0);
  }
}
