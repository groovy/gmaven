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
 * ???
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TypeDef
    extends NamedElement
{
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
        // HACK: Try to keep some sanity, this isn't fool-proof, but should help some to avoid crappy errors.
        if ("Object".equals(name)) {
            super.setName("java.lang.Object");
        }
        else if ("String".equals(name)) {
            super.setName("java.lang.String");
        }
        else {
            super.setName(name);
        }
    }
    
    public String toString() {
        StringBuffer buff = new StringBuffer();

        String name = getName();

        if (name == null) {
            name = "java.lang.Object";
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
            return "null";
        }
        else if (dimensions > 0) {
            return "null";
        }
        else if (name.equals("byte")) {
            return "0";
        }
        else if (name.equals("short")) {
            return "0";
        }
        else if (name.equals("int")) {
            return "0";
        }
        else if (name.equals("long")) {
            return "0L";
        }
        else if (name.equals("float")) {
            return "0.0f";
        }
        else if (name.equals("double")) {
            return "0.0d";
        }
        else if (name.equals("char")) {
            return "'\\u0000'";
        }
        else if (name.equals("boolean")) {
            return "false";
        }
        else {
            return "null";
        }
    }

    public boolean isBoolean() {
        return "boolean".equals(getName());
    }
}