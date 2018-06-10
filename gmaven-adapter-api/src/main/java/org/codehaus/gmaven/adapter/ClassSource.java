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
package org.codehaus.gmaven.adapter;

import java.io.File;
import java.io.Reader;
import java.net.URL;

/**
 * Configuration details for construction of a {@link groovy.lang.GroovyCodeSource} instance.
 *
 * @since 2.0
 */
@SuppressWarnings("JavadocReference")
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
