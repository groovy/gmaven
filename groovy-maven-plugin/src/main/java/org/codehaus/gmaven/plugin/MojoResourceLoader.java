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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.annotation.Nullable;

import org.codehaus.gmaven.adapter.ClassSource;
import org.codehaus.gmaven.adapter.ResourceLoader;

/**
 * Mojo {@link ResourceLoader}.
 *
 * @since 2.0
 */
public class MojoResourceLoader
    extends BasicResourceLoader
{
  private final ClassSource classSource;

  private final List<File> scriptpath;

  public MojoResourceLoader(final ClassLoader classLoader,
                            final @Nullable ClassSource classSource,
                            final @Nullable List<File> scriptpath)
  {
    super(classLoader);
    this.classSource = classSource;
    this.scriptpath = scriptpath;
  }

  public MojoResourceLoader(final ClassLoader classLoader,
                            final @Nullable List<File> scriptpath)
  {
    this(classLoader, null, scriptpath);
  }

  @Nullable
  protected URL resolve(final String className, final ClassLoader classLoader) throws MalformedURLException {
    String name = resourceName(className);

    // First check the scriptpath
    if (scriptpath != null) {
      for (File path : scriptpath) {
        File file = new File(path, name);
        if (file.exists()) {
          return file.toURI().toURL();
        }
      }
    }

    // Then look for a resource in the classpath
    URL url = classLoader.getResource(name);

    // FIXME: Sort out if this is required to function or if it should be removed
    // Try w/o leading '/'... ???  Seems that when loading resources the '/' prefix messes things up?
    if (url == null) {
      if (name.startsWith("/")) {
        String tmp = name.substring(1, name.length());
        url = classLoader.getResource(tmp);
      }
    }

    if (url != null) {
      return url;
    }

    // Check for a class defined in a file next to the main script file
    if (classSource != null) {
      File script = classSource.getFile();
      if (script != null) {
        File file = new File(script.getParentFile(), name);

        if (file.exists()) {
          return file.toURI().toURL();
        }
      }
    }

    // else fallback to super impl
    return super.resolve(className, classLoader);
  }
}