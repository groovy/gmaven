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

import org.apache.maven.shared.io.scan.mapping.SourceMapping;
import org.apache.maven.shared.io.scan.mapping.SuffixMapping;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.plugin.CompilerMojoSupport;
import org.codehaus.groovy.maven.runtime.StubCompiler;

import java.io.File;
import java.io.IOException;

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
 */
public abstract class AbstractGenerateStubsMojo
    extends CompilerMojoSupport
{
    protected AbstractGenerateStubsMojo() {
        super(StubCompiler.KEY);
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
