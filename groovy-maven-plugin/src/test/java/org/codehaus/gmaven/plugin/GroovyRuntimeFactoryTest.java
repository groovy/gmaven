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
package org.codehaus.gmaven.plugin;

import java.util.Iterator;
import java.util.List;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import com.google.common.collect.Lists;
import org.codehaus.gmaven.adapter.GroovyRuntime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link GroovyRuntimeFactory}.
 *
 * These tests assume that the underlying {@link java.util.ServiceLoader} functions,
 * so we by-pass it and only validate the handling around the loader.
 */
public class GroovyRuntimeFactoryTest
    extends TestSupport
{
  private GroovyRuntimeFactory underTest;

  @Mock
  private ClassLoader classLoader;

  private List<GroovyRuntime> services;

  @Before
  public void setUp() throws Exception {
    services = Lists.newArrayList();

    underTest = new GroovyRuntimeFactory()
    {
      @Override
      protected Iterator<GroovyRuntime> findServices(final ClassLoader classLoader) {
        return services.iterator();
      }
    };
  }

  /**
   * If no service is found, ISE should be thrown.
   */
  @Test(expected = IllegalStateException.class)
  public void create_noServices() {
    underTest.create(classLoader);
    fail();
  }

  /**
   * If more than one service is found, ISE should be thrown.
   */
  @Test(expected = IllegalStateException.class)
  public void create_multipuleServices() {
    services.add(mock(GroovyRuntime.class));
    services.add(mock(GroovyRuntime.class));
    underTest.create(classLoader);
    fail();
  }

  /**
   * Single service should return non-null value.
   */
  @Test
  public void create_singleService() {
    services.add(mock(GroovyRuntime.class));
    GroovyRuntime runtime = underTest.create(classLoader);
    assertThat(runtime, notNullValue());
  }
}
