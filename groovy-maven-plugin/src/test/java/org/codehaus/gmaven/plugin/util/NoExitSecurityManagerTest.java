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
  public void exit() {
    System.exit(0);
  }
}
