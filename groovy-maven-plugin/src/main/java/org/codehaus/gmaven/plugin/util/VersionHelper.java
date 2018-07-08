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
import com.google.common.base.Throwables;
import org.codehaus.plexus.component.annotations.Component;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionRange;
import org.eclipse.aether.version.VersionScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Aether version/range/scheme helpers.
 *
 * @since 2.0
 */
@Component(role = VersionHelper.class)
public class VersionHelper
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * Earliest suffix to help Aether detect ranges _better_ when SNAPSHOT versions are used.
   */
  public static final String EARLIEST_SUFFIX = "alpha-SNAPSHOT";

  private final VersionScheme versionScheme;

  public VersionHelper() {
    this(new GenericVersionScheme());
  }

  @VisibleForTesting
  public VersionHelper(final VersionScheme versionScheme) {
    this.versionScheme = checkNotNull(versionScheme);
  }

  public VersionScheme getScheme() {
    return versionScheme;
  }

  /**
   * Parses a {@link Version} and propagates exceptions.
   */
  public Version parseVersion(final String version) {
    checkNotNull(version);
    try {
      return versionScheme.parseVersion(version);
    }
    catch (InvalidVersionSpecificationException e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Parses a {@link VersionRange} and propagates exceptions.
   */
  public VersionRange parseRange(final String pattern) {
    checkNotNull(pattern);
    try {
      return versionScheme.parseVersionRange(pattern);
    }
    catch (InvalidVersionSpecificationException e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Builds a range between version parts and parts[n] + 1.
   *
   * ie. range(1,2,3) produces a range between versions 1.2.3 and 1.2.4.
   */
  public VersionRange range(final int... parts) {
    checkNotNull(parts);
    checkArgument(parts.length != 0);

    StringBuilder buff = new StringBuilder()
        .append("[");

    for (int i = 0; i < parts.length; i++) {
      buff.append(parts[i]);
      if (i + 1 < parts.length) {
        buff.append(".");
      }
    }

    buff.append(",");

    for (int i = 0; i < parts.length; i++) {
      if (i + 1 == parts.length) {
        buff.append(parts[i] + 1);
      }
      else {
        buff.append(parts[i])
            .append(".");
      }
    }

    buff.append(")");

    log.trace("Range pattern: {}", buff);

    return parseRange(buff.toString());
  }

  /**
   * Builds a range before version parts.
   */
  public VersionRange before(final int... parts) {
    checkNotNull(parts);
    checkArgument(parts.length != 0);

    StringBuilder buff = new StringBuilder()
        .append("(,");

    for (int i = 0; i < parts.length; i++) {
      buff.append(parts[i]);
      if (i + 1 < parts.length) {
        buff.append(".");
      }
    }

    buff.append("-")
        .append(EARLIEST_SUFFIX)
        .append(")");

    log.trace("Range pattern: {}", buff);

    return parseRange(buff.toString());
  }
}
