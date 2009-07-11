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

package org.codehaus.groovy.maven.runtime.v1_5.stubgen;

import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ImportDef;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.ModelFactorySupport;
import org.codehaus.groovy.maven.runtime.support.stubgen.model.SourceDef;

/**
 * Creates a stub model for Groovy 1.5.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ModelFactoryImpl
    extends ModelFactorySupport
{
    public ModelFactoryImpl() {
        super(new ParserFactoryImpl());
    }
    
    protected void addDefaultImports(final SourceDef model) {
        assert model != null;

        for (int i=0; i< ResolveVisitor.DEFAULT_IMPORTS.length; i++) {
            ImportDef def = new ImportDef();

            String pkg = ResolveVisitor.DEFAULT_IMPORTS[i];

            // Strip off the last '.' from the import
            if (pkg.endsWith(".")) {
                pkg = pkg.substring(0, pkg.length() - 1);
            }

            def.setPackage(pkg);
            def.setWildcard(true);

            model.addImport(def);
        }

        model.addImport(new ImportDef("java.math", "BigDecimal"));
        model.addImport(new ImportDef("java.math", "BigInteger"));
    }
}