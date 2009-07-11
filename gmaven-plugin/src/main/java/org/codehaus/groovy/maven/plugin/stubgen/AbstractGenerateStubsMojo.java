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

package org.codehaus.groovy.maven.plugin.stubgen;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.io.scan.mapping.SourceMapping;
import org.apache.maven.shared.io.scan.mapping.SuffixMapping;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.plugin.CompilerMojoSupport;
import org.codehaus.groovy.maven.runtime.StubCompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Support for Java stub generation mojos.
 *
 * <p>
 * Stub generation basically parses Groovy sources, and then creates the bare-minimum
 * Java source equivilent so that the maven-compiler-plugin's compile and testCompile
 * goals can execute and resolve Groovy classes that may be referenced by Java sources.
 * </p>
 *
 * <p>
 * This is important, since our compile and testCompile goals execute *after* the 
 * normal Java compiler does.
 * </p>
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @author Jason Smith
 */
public abstract class AbstractGenerateStubsMojo
    extends CompilerMojoSupport
{
    protected AbstractGenerateStubsMojo() {
        super(StubCompiler.KEY);
    }
    
    public void execute() throws MojoExecutionException, MojoFailureException {
    	super.execute();

        // Treatment for MGROOVY-187.
        try {
            resetStubModifiedDates();
        }
        catch (Exception e) {
            throw new MojoExecutionException("Failed to get output folder.", e);
        }
    }
   
    /**
     * Modifies the dates of the created stubs to 1970, ensuring that the Java
     * compiler will not come along and overwrite perfectly good compiled Groovy 
     * just because it has a newer source stub.  Basically, this prevents the 
     * stubs from causing a side effect with the Java compiler, but still allows
     * the stubs to work with JavaDoc.  Ideally, the code for this should be 
     * added to the code that creates the stubs, but as that code is sprinkled 
     * across several different runtimes, I am putting this into the common area.
     */
    private void resetStubModifiedDates() throws Exception {
        List stubs = recurseFiles(getOutputDirectory());

        for (Iterator i = stubs.iterator(); i.hasNext();) {
            File file = (File) i.next();
            file.setLastModified(0L);
        }
    }
    
    /**
     * Get all files, recursively, in a folder.
     * TODO: Should be moved into a utility class.
     *
     * @param folder The folder to look in.
     * @return A list of <code>File</code> instances.
     */
    private List recurseFiles(final File folder) {
        assert folder != null;

        List result = new ArrayList();
        File[] files = folder.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    result.addAll(recurseFiles(files[i]));
                }
                else {
                    result.add(files[i]);
                }
            }
        }

        return result;
    }

    protected abstract void forceCompile(final File file);

    protected void process(final Component component) throws Exception {
        assert component != null;

        StubCompiler compiler = (StubCompiler)component;

        compiler.setTargetDirectory(getOutputDirectory());
        
        // compiler.setClassPath(createClassPath());

        //
        // TODO: Bridge mojo config to component config
        //

        compile(compiler, sources != null ? sources : getDefaultSources());
    }

    protected void compile(final StubCompiler compiler, final FileSet[] sources) throws Exception {
        assert compiler != null;
        assert sources != null;

        // Seems like we have to add the output dir each time so that the m-p-p site muck works
        addSourceRoot(getOutputDirectory());
        
        for (int i=0; i<sources.length; i++) {
            addSourceRoot(sources[i]);

            SourceMapping[] mappings = {
                new SuffixMapping(".groovy", ".java"),
            };

            File[] files = scanForSources(sources[i], mappings);

            for (int j=0; j < files.length; j++) {
                log.debug(" + " + files[j]);

                compiler.add(files[j]);

                // For now assume we compile this puppy
                forceCompile(files[j]);
            }
        }

        int count = compiler.compile();

        if (count == 0) {
            log.info("No sources found for Java stub generation");
        }
        else {
            log.info("Generated " + count + " Java stub" + (count > 1 ? "s" : ""));
        }
    }

    private void addSourceRoot(final FileSet fileSet) throws IOException {
        assert fileSet != null;

        // Hook up as a source root so other plugins (like the m-compiler-p) can process anything in here if needed
        File basedir = new File(fileSet.getDirectory());
        
        addSourceRoot(basedir);
    }
}
