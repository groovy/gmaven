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
  private final Logger log = LoggerFactory.getLogger(getClass());

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
