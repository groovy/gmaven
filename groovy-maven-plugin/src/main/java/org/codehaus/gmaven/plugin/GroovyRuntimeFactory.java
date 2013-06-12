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
import java.util.ServiceLoader;

import com.google.common.annotations.VisibleForTesting;
import org.codehaus.gmaven.adapter.GroovyRuntime;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Creates {@link GroovyRuntime} instances.
 *
 * @since 2.0
 */
@Component(role=GroovyRuntimeFactory.class)
public class GroovyRuntimeFactory
{
  protected final Logger log = LoggerFactory.getLogger(getClass());

  public GroovyRuntime create(final ClassLoader classLoader) {
    checkNotNull(classLoader);

    Iterator<GroovyRuntime> services = findServices(classLoader);

    checkState(services.hasNext(), "No GroovyRuntime service found");

    GroovyRuntime runtime = services.next();

    checkState(!services.hasNext(), "Multiple GroovyRuntime services found");

    log.debug("Runtime: {}", runtime);
    return runtime;
  }

  @VisibleForTesting
  protected Iterator<GroovyRuntime> findServices(final ClassLoader classLoader) {
    ServiceLoader<GroovyRuntime> serviceLoader = ServiceLoader.load(GroovyRuntime.class, classLoader);
    log.debug("Loader: {}", serviceLoader);
    return serviceLoader.iterator();
  }
}
