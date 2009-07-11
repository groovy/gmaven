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
 * Representation of for entity modifiers.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ModifiersDef
    extends Element
{
    public static final String ABSTRACT = "abstract";

    public static final String PUBLIC = "public";

    public static final String PRIVATE = "private";

    public static final String PROTECTED = "protected";

    public static final String STATIC = "static";

    public static final String FINAL = "final";

    public static final String SYNCHRONIZED = "synchronized";

    public static final String TRANSIENT = "transient";

    public static final String VOLATILE = "volatile";

    public static final String NATIVE = "native";

    public static final String STRICTFP = "strictfp";

    private final Set values = new LinkedHashSet();

    public ModifiersDef add(final String modifier) {
        assert modifier != null;

        values.add(modifier);

        return this;
    }

    public ModifiersDef remove(final String modifier) {
        assert modifier != null;

        values.remove(modifier);

        return this;    
    }

    public ModifiersDef merge(final ModifiersDef modifiers) {
        assert modifiers != null;

        Iterator iter = modifiers.getValues().iterator();
        
        while (iter.hasNext()) {
            String modifier = (String)iter.next();
            add(modifier);
        }

        return this;
    }

    public Set getValues() {
        return values;
    }

    private boolean contains(final String modifier) {
        assert modifier != null;

        return values.contains(modifier);
    }
    
    //
    // Helpers
    //

    public boolean isAbstract() {
        return contains(ABSTRACT);
    }

    public boolean isPublic() {
        return contains(PUBLIC);
    }

    public boolean isPrivate() {
        return contains(PRIVATE);
    }

    public boolean isProtected() {
        return contains(PROTECTED);
    }

    public boolean isStatic() {
        return contains(STATIC);
    }

    public boolean isFinal() {
        return contains(FINAL);
    }

    public boolean isSynchronized() {
        return contains(SYNCHRONIZED);
    }

    public boolean isTransient() {
        return contains(TRANSIENT);
    }

	public boolean isVolatile() {
        return contains(VOLATILE);
    }

	public boolean isNative() {
        return contains(NATIVE);
    }

	public boolean isStrictfp() {
        return contains(STRICTFP);
    }

    public boolean hasAccessModifiers() {
        return
            isPrivate() ||
            isProtected() ||
            isPublic();
    }
}