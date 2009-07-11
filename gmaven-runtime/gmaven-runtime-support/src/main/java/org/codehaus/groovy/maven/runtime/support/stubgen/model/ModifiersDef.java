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
 * Container for entity modifiers, essentially a set of strings.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ModifiersDef
    extends Element
{
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
        return contains("abstract");
    }

    public boolean isPublic() {
        return contains("public");
    }

    public boolean isPrivate() {
        return contains("private");
    }

    public boolean isProtected() {
        return contains("protected");
    }

    public boolean isStatic() {
        return contains("static");
    }

    public boolean isFinal() {
        return contains("final");
    }

    public boolean isSynchronized() {
        return contains("synchronized");
    }

    public boolean isTransient() {
        return contains("transient");
    }

	public boolean isVolatile() {
        return contains("volatile");
    }

	public boolean isNative() {
        return contains("native");
    }

	public boolean isStrictfp() {
        return contains("strictfp");
    }

    public boolean hasAccessModifiers() {
        return
            isPrivate() ||
            isProtected() ||
            isPublic();
    }
}