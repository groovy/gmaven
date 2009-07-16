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
 * Representation of a field definition.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class FieldDef
    extends Entity
{
    private ClassDef parent;

    private TypeDef type;

    public ClassDef getParent() {
        return parent;
    }

    public void setParent(final ClassDef parent) {
        this.parent = parent;
    }

    public TypeDef getType() {
        return type;
    }

    public void setType(final TypeDef type) {
        this.type = type;
    }

    public boolean isProperty() {
        ModifiersDef modifiers = getModifiers();

        assert parent != null;
        
        return !parent.isInterface() && !modifiers.hasAccessModifiers();
    }
}