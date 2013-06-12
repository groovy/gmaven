/*
 * Copyright (C) 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.gmaven.plugin.util;

import java.security.Permission;

import javax.annotation.Nullable;

/**
 * Custom security manager to {@link System#exit} (and related) from being used.
 *
 * @since 2.0
 */
public class NoExitSecurityManager
    extends SecurityManager
{
  private final SecurityManager parent;

  public NoExitSecurityManager(final @Nullable SecurityManager parent) {
    this.parent = parent;
  }

  // NOTE: Do not log/print anything from this implementation, as it can result in stack overflow
  // NOTE: due to console/shell installing system out/err interceptors.

  private void fail() {
    throw new SecurityException("Use of System.exit() is forbidden");
  }

  @Override
  public void checkPermission(final Permission perm) {
    if (perm == null) {
      return;
    }
    String name = perm.getName();
    if (name != null && name.startsWith("exitVM")) {
      fail();
    }
    if (parent != null) {
      parent.checkPermission(perm);
    }
  }

  /**
   * Always throws {@link SecurityException}.
   */
  @Override
  public void checkExit(final int code) {
    fail();
  }
}