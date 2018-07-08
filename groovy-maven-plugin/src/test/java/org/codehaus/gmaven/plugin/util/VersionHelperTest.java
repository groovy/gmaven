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

import org.eclipse.aether.version.VersionRange;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link VersionHelper}.
 */
public class VersionHelperTest
    extends TestSupport
{
  private VersionHelper underTest;

  @Before
  public void setUp() throws Exception {
    underTest = new VersionHelper();
  }

  @Test
  public void range() {
    VersionRange range = underTest.range(1, 2, 3);
    log(range);
    assertThat(range.toString(), is("[1.2.3,1.2.4)"));
  }

  @Test
  public void before() {
    VersionRange range = underTest.before(1, 2, 3);
    log(range);
    assertThat(range.toString(), is("(,1.2.3-" + VersionHelper.EARLIEST_SUFFIX + ")"));
  }
}
