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

import java.util.Map;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link PropertiesBuilder}.
 */
public class PropertiesBuilderTest
    extends TestSupport
{
  private PropertiesBuilder underTest;

  @Before
  public void setUp() throws Exception {
    underTest = new PropertiesBuilder();
  }

  @Test
  public void defaults_used() throws Exception {
    underTest.setDefaults(ImmutableMap.of("foo", "bar"));
    underTest.setProperties(ImmutableMap.of("baz", "ick"));
    Map<String, String> props = underTest.build();

    assertThat(props, hasEntry("foo", "bar"));
  }

  @Test
  public void properties_overrideDefaults() throws Exception {
    underTest.setDefaults(ImmutableMap.of("foo", "bar"));
    underTest.setProperties(ImmutableMap.of("foo", "baz"));
    Map<String, String> props = underTest.build();

    assertThat(props, hasEntry("foo", "baz"));
  }
}
