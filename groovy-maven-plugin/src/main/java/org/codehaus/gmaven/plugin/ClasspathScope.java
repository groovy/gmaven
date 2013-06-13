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

/**
 * Classpath scope.
 *
 * @since 2.0
 */
@SuppressWarnings("UnusedDeclaration")
public enum ClasspathScope
{
  none(null),
  provided(null),
  compile(provided),
  runtime(compile),
  test(runtime);

  private final ClasspathScope include;

  private ClasspathScope(final ClasspathScope include) {
    this.include = include;
  }

  public boolean matches(final String scope) {
    if (name().equals(scope)) {
      return true;
    }
    else if (include != null) {
      return include.matches(scope);
    }
    return false;
  }

  public boolean matches(final ClasspathScope scope) {
    return matches(scope.name());
  }
}
