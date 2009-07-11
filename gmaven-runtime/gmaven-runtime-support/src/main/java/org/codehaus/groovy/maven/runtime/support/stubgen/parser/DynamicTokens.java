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

package org.codehaus.groovy.maven.runtime.support.stubgen.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Discovers token names and values from a class dynamically.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @version $Id$
 */
public class DynamicTokens
    implements Tokens
{
    /** Maps token name to value. */
    private final Map names = new LinkedHashMap();

    /** Maps token value to name. */
    private final Map values = new LinkedHashMap();

    public DynamicTokens(final Class type) {
        assert type != null;

        try {
            discover(type);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void discover(final Class type) throws IllegalAccessException {
        assert type != null;

        Field[] fields = type.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            discover(fields[i]);
        }
    }

    private void discover(final Field field) throws IllegalAccessException {
        assert field != null;

        int mod = field.getModifiers();

        if (Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod)) {
            String name = field.getName();
            Class type = field.getType();
            Object value = field.get(type);

            // Token types are only numbers, so ignore everything else
            if (value instanceof Number) {
                names.put(name, value);
                values.put(value, name);
            }
        }
    }

    public int value(final String name) {
        assert name != null;

        Number value = (Number) names.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Unknown token name: " + name);
        }

        return value.intValue();
    }

    public String name(final int value) {
        String name = (String)values.get(new Integer(value));
        if (name == null) {
            throw new IllegalArgumentException("Unknown token value: " + value);
        }

        return name;
    }
}
