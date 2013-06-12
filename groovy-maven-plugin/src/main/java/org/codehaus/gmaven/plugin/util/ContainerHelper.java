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
  protected final Logger log = LoggerFactory.getLogger(getClass());

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
