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

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.Nullable;

import org.codehaus.gmaven.adapter.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Basic {@link ResourceLoader} implementation.
 *
 * @since 2.0
 */
public class BasicResourceLoader
    implements ResourceLoader
{
  protected static final String DOT_GROOVY = ".groovy";

  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected final ClassLoader classLoader;

  public BasicResourceLoader(final ClassLoader classLoader) {
    this.classLoader = checkNotNull(classLoader);
  }

  @Nullable
  public URL loadResource(final String name) throws MalformedURLException {
    return resolve(name, classLoader);
  }

  @Nullable
  protected URL resolve(final String className, final ClassLoader classLoader) throws MalformedURLException {
    log.trace("Resolve; class-name: {}, class-loader: {}", className, classLoader);

    String name = resourceName(className);

    log.trace("Resolve; resource: {}", name);

    boolean tcl = false;
    URL resource = classLoader.getResource(name);

    // try from tcl if given CL does not resolve
    if (resource == null) {
      resource = Thread.currentThread().getContextClassLoader().getResource(name);
      tcl = true;
    }

    log.trace("Resource: {}, tcl: {}", resource, tcl);

    return resource;
  }

  /**
   * Translate class-name into resource-name.
   */
  protected String resourceName(final String className) {
    String name = className;

    if (!name.startsWith("/")) {
      name = "/" + name;
    }

    if (!name.endsWith(DOT_GROOVY)) {
      name = name.replace('.', '/');
      name += DOT_GROOVY;
    }

    return name;
  }
}