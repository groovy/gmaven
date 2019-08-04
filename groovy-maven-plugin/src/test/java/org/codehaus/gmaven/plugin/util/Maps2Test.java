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

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.junit.Test;

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;

/**
 * {@link Maps2} tests.
 */
public class Maps2Test
    extends TestSupport
{
  @Test
  public void fromProperties_entryWithNullValue() throws Exception {
    Properties props1 = new Properties() {
      @Override
      public Enumeration<?> propertyNames() {
        return Iterators.asEnumeration(ImmutableList.of("foo").iterator());
      }

      @Override
      public String getProperty(final String key) {
        if ("foo".equals(key)) {
          return null;
        }
        return super.getProperty(key);
      }
    };
    Map<String, String> props2 = Maps2.fromProperties(props1);
    assertThat(props2, hasEntry("foo", null));
  }
}
