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

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Provides the ability to execute a Groovy script.
 *
 * @since 2.0
 *
 * @see groovy.lang.GroovyShell
 */
@SuppressWarnings("JavadocReference")
public interface ScriptExecutor
{
  @Nullable
  Object execute(ClassSource classSource,
                 ClassLoader classLoader,
                 ResourceLoader resourceLoader,
                 Map<String, Object> context,
                 @Nullable Map<String, Object> options)
      throws Exception;
}