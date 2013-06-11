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
 * so we by-pass it and only validate the handling around the loaders.
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
