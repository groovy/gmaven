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

package org.codehaus.groovy.maven.plugin.compile;

import org.apache.maven.shared.io.scan.mapping.SourceMapping;
import org.apache.maven.shared.io.scan.mapping.SuffixMapping;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.feature.Configuration;
import org.codehaus.groovy.maven.plugin.CompilerMojoSupport;
import org.codehaus.groovy.maven.runtime.ClassCompiler;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * Support for compile mojos that generate classes.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class AbstractCompileMojo
    extends CompilerMojoSupport
    implements ClassCompiler.Keys
{
    protected AbstractCompileMojo() {
        super(ClassCompiler.KEY);
    }

    /**
     * Sets the encoding to be used when reading source files.
     *
     * @parameter expression="${sourceEncoding}" default-value="${file.encoding}"
     * 
     * @noinspection UnusedDeclaration
     */
    private String sourceEncoding;

    /**
     * Turns verbose operation on or off.
     *
     * @parameter expression="${verbose}" default-value="false"
     *
     * @noinspection UnusedDeclaration
     */
    private boolean verbose;

    /**
     * Turns debugging operation on or off.
     *
     * @parameter expression="${debug}" default-value="false"
     *
     * @noinspection UnusedDeclaration
     */
    private boolean debug;

    /**
     * Enable compiler to report stack trace information if a problem occurs.
     *
     * @parameter expression="${stacktrace}" default-value="false"
     *
     * @noinspection UnusedDeclaration
     */
    private boolean stacktrace;

    /**
     * Sets the error tolerance, which is the number of non-fatal errors (per unit)
     * that should be tolerated before compilation is aborted.
     *
     * @parameter expression="${tolerance}" default-value="0"
     *
     * @noinspection UnusedDeclaration
     */
    private int tolerance;
    
    /**
     * Allow setting the bytecode compatibility.
     *
     * @parameter expression="${targetBytecode}"
     *
     * @noinspection UnusedDeclaration
     */
    private String targetBytecode;
    
    /**
     * Sets the warning level.
     *
     * @paramater expression="${waningLevel}" default-value="0"
     *
     * @noinspection UnusedDeclaration
     */
    private int warningLevel;
    
    /**
     * Sets the name of the base class for scripts. It must be a subclass of <tt>groovy.lang.Script</tt>.
     *
     * @parameter expression="${scriptBaseClassname}"
     *
     * @noinspection UnusedDeclaration
     */ 
    private String scriptBaseClassname;

    /**
     * Set the default extention for Groovy script source files.
     *
     * @parameter expression="${defaultScriptExtension}" default-value=".groovy"
     *
     * @noinspection UnusedDeclaration
     */
    private String defaultScriptExtension;

    protected abstract Set getForcedCompileSources();

    protected void process(final Component component) throws Exception {
        assert component != null;

        ClassCompiler compiler = (ClassCompiler)component;

        compiler.setTargetDirectory(getOutputDirectory());
        
        compiler.setClassPath(createClassPath());

        Configuration config = component.config();

        config.set(VERBOSE, verbose);

        config.set(DEBUG, debug);

        config.set(TOLERANCE, tolerance);
        
        if (targetBytecode != null) {
            config.set(TARGET_BYTECODE, targetBytecode);
        }

        config.set(WARNING_LEVEL, warningLevel);
        
        if (sourceEncoding != null) {
            config.set(SOURCE_ENCODING, sourceEncoding);
        }

        if (scriptBaseClassname != null) {
            config.set(SCRIPT_BASE_CLASSNAME, scriptBaseClassname);
        }
        
        if (defaultScriptExtension != null) {
            config.set(DEFAULT_SCRIPT_EXTENSION, defaultScriptExtension);
        }

        compile(compiler, sources != null ? sources : getDefaultSources());
    }

    protected void compile(final ClassCompiler compiler, final FileSet[] sources) throws Exception {
        assert compiler != null;
        assert sources != null;

        for (int i=0; i<sources.length; i++) {
            SourceMapping[] mappings = {
                new SuffixMapping(".groovy", ".class"),
                new SuffixMapping(".java", ".class"),
            };

            File[] files = scanForSources(sources[i], mappings);

            for (int j=0; j < files.length; j++) {
                log.debug(" + " + files[j]);

                compiler.add(files[j]);
            }
        }

        Set forced = getForcedCompileSources();

        if (!forced.isEmpty()) {
            log.debug("Forcing to compile:");

            for (Iterator iter=forced.iterator(); iter.hasNext();) {
                File file = (File)iter.next();

                log.debug(" + {}", file);

                compiler.add(file);
            }
        }

        int count = compiler.compile();

        if (count == 0) {
            log.info("No sources found to compile");
        }
        else {
            log.info("Compiled " + count + " Groovy class" + (count > 1 ? "es" : ""));
        }
    }
}
