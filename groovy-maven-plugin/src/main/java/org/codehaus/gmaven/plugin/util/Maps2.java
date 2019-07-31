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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Map helper.
 *
 * @since 2.1.1
 */
public class Maps2
{
  private Maps2() {
    // empty
  }

  public static Map<String,String> fromProperties(final Properties properties) {
    checkNotNull(properties);
    Map<String,String> result = new HashMap<String, String>(properties.size());
    Enumeration names = properties.propertyNames();
    while (names.hasMoreElements()) {
      String name = (String)names.nextElement();
      result.put(name, properties.getProperty(name));
    }
    return result;
  }
}
