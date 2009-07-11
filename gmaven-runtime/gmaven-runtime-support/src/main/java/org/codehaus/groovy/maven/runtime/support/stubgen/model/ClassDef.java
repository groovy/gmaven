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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ???
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ClassDef
    extends Entity
{
    private final Type type;

    private SourceDef parent;

    private TypeDef superClass;

    private Set implementz = new LinkedHashSet();

    private Set fields = new LinkedHashSet();

    private Set methods = new LinkedHashSet();

    public ClassDef() {
        this(Type.CLASS);
    }

    protected ClassDef(final Type type) {
        assert type != null;

        this.type = type;
    }

    public SourceDef getParent() {
        return parent;
    }

    public void setParent(final SourceDef parent) {
        this.parent = parent;
    }

    public Type getType() {
        return type;
    }

    public boolean isInterface() {
        return type == Type.INTERFACE;
    }

    public boolean isEnum() {
        return type == Type.ENUM;
    }

    public PackageDef getPackage() {
        if (parent == null) {
            return null;
        }

        return parent.getPackage();
    }

    public Set getImports() {
        if (parent == null) {
            return Collections.EMPTY_SET;
        }

        return parent.getImports();
    }
    
    public TypeDef getSuperClass() {
        return superClass;
    }

    public void setSuperClass(final TypeDef type) {
        this.superClass = type;
    }

    public void addImplements(final TypeDef type) {
        assert type != null;

        implementz.add(type);
    }

    public Set getImplements() {
        return implementz;
    }

    public void addField(final FieldDef def) {
        assert def != null;

        def.setParent(this);
        fields.add(def);
    }

    public Set getFields() {
        return fields;
    }

    public void addConstructor(final ConstructorDef def) {
        assert def != null;

        def.setParent(this);
        methods.add(def);
    }

    public void addMethod(final MethodDef def) {
        assert def != null;

        def.setParent(this);
        methods.add(def);
    }

    public Set getMethods() {
        return methods;
    }

    public Set getConstructors() {
        Set set = new LinkedHashSet();

        Iterator iter = getMethods().iterator();
        while (iter.hasNext()) {
            MethodDef def = (MethodDef)iter.next();
            if (def.isConstructor()) {
                set.add(def);
            }
        }

        return set;
    }

    //
    // Type
    //

    /**
     * Psuedo-enum for class type.
     */
    public static final class Type
    {
        public static final String CLASS_NAME = "class";

        public static final int CLASS_CODE = 0;

        public static final Type CLASS = new Type(CLASS_NAME, CLASS_CODE);

        public static final String INTERFACE_NAME = "interface";

        public static final int INTERFACE_CODE = 1;

        public static final Type INTERFACE = new Type(INTERFACE_NAME, INTERFACE_CODE);

        public static final String ENUM_NAME = "enum";

        public static final int ENUM_CODE = 2;

        public static final Type ENUM = new Type(ENUM_NAME, ENUM_CODE);

        public final String name;

        public final int code;

        private Type(final String name, final int code) {
            assert name != null;

            this.name = name;
            this.code = code;
        }

        public String toString() {
            return name;
        }
    }
}