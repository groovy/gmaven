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

package org.codehaus.gmaven.runtime.v1_7;

import org.codehaus.gmaven.feature.Component;
import org.codehaus.gmaven.feature.support.FeatureSupport;
import org.codehaus.gmaven.runtime.StubCompiler;
import org.codehaus.gmaven.runtime.support.CompilerSupport;
import org.codehaus.groovy.tools.javac.JavaAwareCompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.Phases;

import java.net.URL;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.security.CodeSource;

import groovy.lang.GroovyClassLoader;

/**
 * Provides the stub compilation feature.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class StubCompilerFeature
    extends FeatureSupport
{
    public StubCompilerFeature() {
        super(StubCompiler.KEY);
    }

    @Override
    protected Component doCreate() throws Exception {
        return new StubCompilerImpl();
    }

    StubCompilerImpl createInternal() throws Exception {
        return new StubCompilerImpl();
    }

    //
    // StubCompilerImpl
    //

    private class StubCompilerImpl
        extends CompilerSupport
        implements StubCompiler
    {
        private CompilerConfiguration cc = new CompilerConfiguration();

        private StubCompilerImpl() throws Exception {
            super(StubCompilerFeature.this);
        }

        public int compile() throws Exception {
            if (sources.isEmpty()) {
                log.debug("No sources added to compile; skipping");
                return 0;
            }

            Map<String,Object> options = new HashMap<String,Object>();
            options.put("stubDir", getTargetDirectory());
            cc.setJointCompilationOptions(options);
            ClassLoader parent = ClassLoader.getSystemClassLoader();

            GroovyClassLoader gcl = new GroovyClassLoader(parent, cc);

            log.debug("Classpath:");

            // Append each URL to the GCL
            URL[] classpath = getClassPath();

            for (int i=0; i<classpath.length; i++) {
                gcl.addURL(classpath[i]);
                log.debug("    {}", classpath[i]);
            }

            CodeSource security = null;
            GroovyClassLoader transformLoader = new GroovyClassLoader(getClass().getClassLoader());
            for (int i=0; i<classpath.length; i++) {
                transformLoader.addURL(classpath[i]);
            }

            JavaStubCompilationUnit cu = new JavaStubCompilationUnit(cc, gcl);

            log.debug("Compiling {} stubs for source(s)", sources.size());

            for (Iterator iter = sources.iterator(); iter.hasNext();) {
                URL url = (URL) iter.next();
                log.debug("    {}", url);
                cu.addSource(url);
            }

            cu.compile();

            int count = cu.getStubCount();
            log.debug("Compiled {} stubs", String.valueOf(count));

            return count;
        }
    }
}