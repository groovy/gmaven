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

package org.codehaus.groovy.maven.runtime.v1_6;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.feature.support.FeatureSupport;
import org.codehaus.groovy.maven.runtime.ClassCompiler;
import org.codehaus.groovy.maven.runtime.support.CompilerSupport;
import org.codehaus.groovy.tools.GroovyClass;

import java.net.URL;
import java.security.CodeSource;
import java.util.Iterator;
import java.util.List;

/**
 * Provides the class compilation feature.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ClassCompilerFeature
    extends FeatureSupport
{
    public ClassCompilerFeature() {
        super(ClassCompiler.KEY);
    }

    protected Component doCreate() throws Exception {
        return new ClassCompilerImpl();
    }

    //
    // ClassCompilerImpl
    //
    
    private class ClassCompilerImpl
        extends CompilerSupport
        implements ClassCompiler, ClassCompiler.Keys
    {
        private final CompilerConfiguration cc = new CompilerConfiguration();

        private URL[] classPath;

        private ClassCompilerImpl() throws Exception {
            super(ClassCompilerFeature.this);

            cc.setVerbose(config.get(VERBOSE, false));

            cc.setDebug(config.get(DEBUG, false));

            if (config.contains(TOLERANCE)) {
                cc.setTolerance(config.get(TOLERANCE, 0));
            }
            
            if (config.contains(TARGET_BYTECODE)) {
                cc.setTargetBytecode(config.get(TARGET_BYTECODE, (String)null));
            }

            if (config.contains(SCRIPT_BASE_CLASSNAME)) {
                cc.setScriptBaseClass(config.get(SCRIPT_BASE_CLASSNAME, (String)null));
            }

            if (config.contains(DEFAULT_SCRIPT_EXTENSION)) {
                cc.setDefaultScriptExtension(config.get(DEFAULT_SCRIPT_EXTENSION, (String)null));
            }
            
            if (config.contains(WARNING_LEVEL)) {
                cc.setTolerance(config.get(WARNING_LEVEL, 0));
            }
            
            if (config.contains(SOURCE_ENCODING)) {
                cc.setSourceEncoding(config.get(SOURCE_ENCODING, (String)null));
            }
        }

        public void setClassPath(final URL[] urls) {
            assert urls != null && urls.length > 0;

            this.classPath = urls;
        }

        public URL[] getClassPath() {
            if (classPath == null || classPath.length == 0) {
                throw new IllegalStateException("Classpath not bound, or is empty");
            }
            
            return classPath;
        }

        public int compile() throws Exception {
            if (sources.isEmpty()) {
                log.debug("No sources added to compile; skipping");

                return 0;
            }

            cc.setTargetDirectory(getTargetDirectory().getCanonicalPath());

            //
            // NOTE: Do not use the CL from this class or it will mess up resolution
            //       when using classes from groovy* which depend on other artifacts,
            //       also don't really want to pollute the classpath with our dependencies.
            //

            ClassLoader parent = ClassLoader.getSystemClassLoader();

            GroovyClassLoader gcl = new GroovyClassLoader(parent, cc);

            log.debug("Classpath:");

            // Append each URL to the GCL
            URL[] classpath = getClassPath();
            
            for (int i=0; i<classpath.length; i++) {
                gcl.addURL(classpath[i]);

                log.debug("    {}", classpath[i]);
            }

            //
            // TODO: See if we should set the CodeSource to something?
            //
            
            CodeSource security = null;
            GroovyClassLoader transformLoader = new GroovyClassLoader(getClass().getClassLoader());
            for (int i=0; i<classpath.length; i++) {
                transformLoader.addURL(classpath[i]);
            }
          
            CompilationUnit cu = new CompilationUnit(cc, security, gcl, transformLoader);
            log.debug("Compiling {} sources", String.valueOf(sources.size()));

            for (Iterator iter = sources.iterator(); iter.hasNext();) {
                URL url = (URL) iter.next();
                log.debug("    {}", url);

                cu.addSource(url);
            }

            cu.compile();

            List classes = cu.getClasses();

            if (log.isDebugEnabled()) {
                log.debug("Compiled {} classes:", String.valueOf(classes.size()));

                for (Iterator iter = classes.iterator(); iter.hasNext();) {
                    log.debug("    {}", ((GroovyClass)iter.next()).getName());
                }
            }

            return classes.size();
        }
    }
}