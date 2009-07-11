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
 * Representation of an import definition.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ImportDef
    extends Element
{
    private boolean isStatic;

    private String packageName;

    private String type;

    private boolean wildcard;

    private String alias;

    public ImportDef() {}

    public ImportDef(final String packageName, final String type) {
        setPackage(packageName);
        setType(type);
    }
    
    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(final boolean flag) {
        this.isStatic = flag;
    }

    public String getPackage() {
        return packageName;
    }

    public void setPackage(final String name) {
        this.packageName = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public boolean isWildcard() {
        return wildcard;
    }

    public void setWildcard(final boolean flag) {
        this.wildcard = flag;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    public String getQualifiedName() {
        StringBuffer buff = new StringBuffer();

        if (packageName != null) {
            buff.append(packageName);

            if (type != null) {
                buff.append(".");
            }
        }

        if (type != null) {
            buff.append(type);
        }

        return buff.toString();
    }
}