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

import com.google.common.annotations.VisibleForTesting;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * IoC container helper.
 *
 * @since 2.0
 */
@SuppressWarnings("unchecked")
@Component(role = ContainerHelper.class)
public class ContainerHelper
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Requirement
  private PlexusContainer container;

  public ContainerHelper() {}

  @VisibleForTesting
  public ContainerHelper(final PlexusContainer container) {
    this.container = checkNotNull(container);
  }

  public Object lookup(final Class type) throws Exception {
    checkNotNull(type);

    log.trace("Lookup; class: {}", type);
    Object component = container.lookup(type);
    log.trace("Component: {}", component);

    return component;
  }

  public Object lookup(final String typeName) throws Exception {
    checkNotNull(typeName);

    log.trace("Lookup; class-name: {}", typeName);
    Object component = container.lookup(typeName);
    log.trace("Component: {}", component);

    return component;
  }

  public Object lookup(final Class type, final String name) throws Exception {
    checkNotNull(type);
    checkNotNull(name);

    log.trace("Lookup; class: {}, name: {}", type, name);
    Object component = container.lookup(type, name);
    log.trace("Component: {}", component);

    return component;
  }

  public Object lookup(final String typeName, final String name) throws Exception {
    checkNotNull(typeName);
    checkNotNull(name);

    log.trace("Lookup; class-name: {}, name: {}", typeName, name);
    Object component = container.lookup(typeName, name);
    log.trace("Component: {}", component);

    return component;
  }
}
