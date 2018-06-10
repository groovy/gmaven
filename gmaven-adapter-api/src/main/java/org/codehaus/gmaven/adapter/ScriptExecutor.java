/*
 * Copyright (c) 2006-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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