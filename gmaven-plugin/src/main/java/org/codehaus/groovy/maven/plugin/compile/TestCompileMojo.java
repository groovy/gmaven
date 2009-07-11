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

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.shared.model.fileset.FileSet;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Compiles Groovy <em>test</em> sources.
 *
 * @goal testCompile
 * @phase test-compile
 * @requiresDependencyResolution test
 * @since 1.0-alpha-1
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TestCompileMojo
    extends AbstractCompileMojo
{
    /**
     * The directory where generated Java class files will be placed.
     *
     * @parameter default-value="${project.build.testOutputDirectory}"
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    private File outputDirectory;
    
    /**
     * Flag to allow test compiliation to be skipped.
     *
     * @parameter expression="${maven.test.skip}" default-value="false"
     *
     * @noinspection UnusedDeclaration
     */
    private boolean skip;
    
    protected List getProjectClasspathElements() throws DependencyResolutionRequiredException {
        return project.getTestClasspathElements();
    }

    protected File getOutputDirectory() {
        return outputDirectory;
    }

    protected List getSourceRoots() {
        return project.getTestCompileSourceRoots();
    }

    protected FileSet[] getDefaultSources() {
        FileSet set = new FileSet();

        File basedir = new File(project.getBasedir(), "src/test/groovy");
        set.setDirectory(basedir.getAbsolutePath());
        set.addInclude("**/*.groovy");

        return new FileSet[] { set };
    }

    protected Set getForcedCompileSources() {
        return compileState.getForcedCompilationTestSources(project);
    }

    protected void doExecute() throws Exception {
        if (skip) {
            log.info("Test compiliation is skipped");
        }
        else {
            super.doExecute();
        }
    }
}
