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
package org.codehaus.gmaven.adapter;

import java.io.File;
import java.io.Reader;
import java.net.URL;

/**
 * Configuration details for construction of a {@code groovy.lang.GroovyCodeSource} instance.
 *
 * @since 2.0
 */
public interface ClassSource
{
  URL getUrl();

  File getFile();

  interface Inline
  {
    String getName();

    String getCodeBase();

    Reader getInput();
  }

  Inline getInline();
}
