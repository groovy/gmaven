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

package org.codehaus.groovy.maven.runtime.support.stubgen.model;

/**
 * Representation of a type definition.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TypeDef
    extends NamedElement
{
    public static final String BYTE = "byte";

    public static final String SHORT = "short";

    public static final String INT = "int";

    public static final String LONG = "long";

    public static final String FLOAT = "float";

    public static final String DOUBLE = "double";

    public static final String CHAR = "char";

    public static final String BOOLEAN = "boolean";

    public static final String OBJECT = Object.class.getName();

    public static final String STRING = String.class.getName();

    public static final String BIG_INT = java.math.BigInteger.class.getName();

    public static final String BIG_DECIMAL = java.math.BigDecimal.class.getName();

    public static final String VOID = "void";

    public static final String NULL = "null";

    private int dimensions;

    public TypeDef() {}

    public TypeDef(final String name) {
        setName(name);
    }

    public TypeDef(final String name, final int n) {
        setName(name);
        setDimensions(n);

    }
    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(final int n) {
        this.dimensions = n;
    }

    public void setName(final String name) {
        // NOTE: Try to keep some sanity, this isn't fool-proof, but should help some to avoid crappy errors.
        if ("Object".equals(name)) {
            super.setName(OBJECT);
        }
        else if ("String".equals(name)) {
            super.setName(STRING);
        }
        else {
            super.setName(name);
        }
    }
    
    public String toString() {
        StringBuffer buff = new StringBuffer();

        String name = getName();

        if (name == null) {
            name = OBJECT;
        }

        buff.append(name);

        if (dimensions > 0) {
            buff.append("[");
            buff.append(dimensions);
        }

        return buff.toString();
    }

    public String getDefaultValue() {
        String name = getName();

        //
        // NOTE: Default values taken from the "Default Values" section of:
        //       http://java.sun.com/docs/books/tutorial/java/nutsandbolts/datatypes.html
        //

        if (name == null) {
            return NULL;
        }
        else if (dimensions > 0) {
            return NULL;
        }
        else if (name.equals(BYTE)) {
            return "0";
        }
        else if (name.equals(SHORT)) {
            return "0";
        }
        else if (name.equals(INT)) {
            return "0";
        }
        else if (name.equals(LONG)) {
            return "0L";
        }
        else if (name.equals(FLOAT)) {
            return "0.0f";
        }
        else if (name.equals(DOUBLE)) {
            return "0.0d";
        }
        else if (name.equals(CHAR)) {
            return "'\\u0000'";
        }
        else if (name.equals(BOOLEAN)) {
            return "false";
        }
        else {
            return NULL;
        }
    }

    public boolean isBoolean() {
        return BOOLEAN.equals(getName());
    }
}