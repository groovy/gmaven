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

import org.codehaus.groovy.maven.runtime.support.stubgen.parser.SourceType;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ???
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SourceDef
    extends Element
{
    private URL url;

    private SourceType type;

    // TODO: Add source-level comment for legal header muck
    
    private PackageDef pkg;

    private Set imports = new LinkedHashSet();

    private Set classes = new LinkedHashSet();

    public URL getUrl() {
        return url;
    }

    public void setUrl(final URL url) {
        this.url = url;
    }

    public SourceType getType() {
        return type;
    }

    public void setType(final SourceType type) {
        this.type = type;
    }

    public PackageDef getPackage() {
        return pkg;
    }

    public void setPackage(final PackageDef pkg) {
        this.pkg = pkg;
    }

    public void addImport(final ImportDef imp) {
        assert imp != null;

        imports.add(imp);
    }

    public Set getImports() {
        return imports;
    }

    public void addClass(final ClassDef def) {
        assert def != null;

        def.setParent(this);
        classes.add(def);
    }

    public Set getClasses() {
        return classes;
    }
}