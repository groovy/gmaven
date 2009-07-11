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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representation of a constructor definition.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ConstructorDef
    extends MethodDef
{
    public static final String SUPER = "super";

    public static final String THIS = "this";
    
    private final boolean magic;

    private String superType;

    private final Set superParameters = new LinkedHashSet();

    public ConstructorDef() {
        this(false);
    }

    public ConstructorDef(final boolean magic) {
        super(Type.CTOR);

        this.magic = magic;
    }

    public boolean isMagic() {
        return magic;
    }

    public TypeDef getReturns() {
        throw new UnsupportedOperationException();
    }

    public void setReturns(final TypeDef returns) {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return getParent().getName();    
    }

    public void setName(final String name) {
        throw new UnsupportedOperationException();
    }

    public String getSuperType() {
        return superType;
    }

    public void setSuperType(final String type) {
        this.superType = type;
    }

    public Set getSuperParameters() {
        return superParameters;
    }

    public void addSuperParameter(final SuperParameterDef def) {
        assert def != null;

        def.setParent(this);
        superParameters.add(def);
    }
}