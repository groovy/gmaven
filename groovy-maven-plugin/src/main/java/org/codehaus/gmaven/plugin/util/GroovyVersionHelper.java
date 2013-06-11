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

import java.lang.reflect.Method;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Groovy version helper.
 *
 * @since 2.0
 */
@Component(role=GroovyVersionHelper.class)
public class GroovyVersionHelper
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Requirement
  private VersionHelper versionHelper;

  public GroovyVersionHelper() {}

  @VisibleForTesting
  public GroovyVersionHelper(final VersionHelper versionHelper) {
    this.versionHelper = checkNotNull(versionHelper);
  }

  @Nullable
  public Version detectVersion(final ClassLoader classLoader) {
    checkNotNull(classLoader);

    log.trace("Detecting Groovy version; class-loader: {}", classLoader);

    // Modern versions of Groovy expose the version via GroovySystem.getVersion()
    String raw = getVersion(classLoader, "groovy.lang.GroovySystem", "getVersion");
    if (raw == null) {
      // Older versions of Groovy expose the version via InvokerHelper.getVersion()
      raw = getVersion(classLoader, "org.codehaus.groovy.runtime.InvokerHelper", "getVersion");
    }

    log.trace("Raw version: {}", raw);
    if (raw == null) {
      return null;
    }

    Version version = versionHelper.parseVersion(raw);
    log.trace("Version: {}", version);
    return version;
  }

  /**
   * Get version from Groovy version helper utilities via reflection.
   */
  private String getVersion(final ClassLoader classLoader, final String className, final String methodName) {
    log.trace("Getting version; class-loader: {}, class-name: {}, method-name: {}",
        classLoader, className, methodName);

    try {
      Class<?> type = classLoader.loadClass(className);
      Method method = type.getMethod(methodName);
      Object result = method.invoke(null);
      if (result != null) {
        return result.toString();
      }
    }
    catch (Throwable e) {
      log.trace("Unable determine version from: {}", className);
    }

    return null;
  }
}
