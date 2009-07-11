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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ???
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class MethodDef
    extends Entity
    implements ParametersAware, ThrowsAware
{
    private final Type type;

    private ClassDef parent;
    
    private TypeDef returns;

    private Set parameters = new LinkedHashSet();

    private Set throwz = new LinkedHashSet();

    public MethodDef() {
        this(Type.METHOD);
    }

    protected MethodDef(final Type type) {
        assert type != null;

        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isConstructor() {
        return type == Type.CTOR;
    }
    
    public ClassDef getParent() {
        return parent;
    }

    public void setParent(final ClassDef parent) {
        this.parent = parent;
    }

    public TypeDef getReturns() {
        return returns;
    }

    public void setReturns(final TypeDef returns) {
        this.returns = returns;
    }

    public void addParameter(final ParameterDef param) {
        assert param != null;

        param.setParent(this);
        parameters.add(param);
    }

    public Set getParameters() {
        return parameters;
    }

    public void addThrows(final TypeDef type) {
        assert type != null;

        throwz.add(type);
    }

    public Set getThrows() {
        return throwz;
    }

    public Set getSuperParameters() {
        throw new UnsupportedOperationException();
    }

    public void setSuperParameters(final Set superParameters) {
        throw new UnsupportedOperationException();
    }

    public String signature() {
        StringBuffer buff = new StringBuffer();

        buff.append(getName());
        buff.append("(");

        Iterator iter = getParameters().iterator();

        while (iter.hasNext()) {
            // FIXME: This does not take into account fully defined types, vs. partially defined, vs. aliased
            
            ParameterDef param = (ParameterDef)iter.next();
            buff.append(param.getType());

            if (iter.hasNext()) {
                buff.append(",");
            }
        }

        buff.append(")");

        return buff.toString();
    }
    
    //
    // Type
    //

    /**
     * Psuedo-enum for method type.
     */
    public static final class Type
    {
        public static final String METHOD_NAME = "method";

        public static final int METHOD_CODE = 0;

        public static final Type METHOD = new Type(METHOD_NAME, METHOD_CODE);

        public static final String CTOR_NAME = "ctor";

        public static final int CTOR_CODE = 1;

        public static final Type CTOR = new Type(CTOR_NAME, CTOR_CODE);

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