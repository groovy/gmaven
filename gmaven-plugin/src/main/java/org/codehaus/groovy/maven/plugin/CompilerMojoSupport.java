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

package org.codehaus.groovy.maven.plugin;

import org.apache.maven.shared.io.scan.StaleResourceScanner;
import org.apache.maven.shared.io.scan.mapping.SourceMapping;
import org.apache.maven.shared.model.fileset.FileSet;
import org.codehaus.groovy.maven.common.ArtifactItem;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Support for compile mojos (class and stub gen).
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class CompilerMojoSupport
    extends ComponentMojoSupport
{
    /**
     * Additional artifacts to add to the classpath (in addition to the classpath
     * which is picked up from the executing poms configuration).
     *
     * @parameter
     */
    protected ArtifactItem[] classpath;

    /**
     * Source files to be included.  If not specified, then the default will be used.
     *
     * @parameter
     */
    protected FileSet[] sources;

    /**
     * @component
     * 
     * @noinspection UnusedDeclaration
     */
    protected CompileState compileState;

    protected CompilerMojoSupport(final String key) {
        super(key);
    }

    //
    // Support
    //

    protected abstract File getOutputDirectory() throws Exception;

    protected abstract List getSourceRoots();

    protected void addSourceRoot(final File dir) throws IOException {
        assert dir != null;

        List roots = getSourceRoots();
        assert roots != null;
        
        String path = dir.getCanonicalPath();

        if (!roots.contains(path)) {
            log.debug("Adding source root: {}", path);
            
            roots.add(path);
        }
    }

    protected abstract FileSet[] getDefaultSources();

    protected ArtifactItem[] getUserClassspathElements() {
        return classpath;
    }

    //
    // File Scanning
    //

    protected Set getIncludesFrom(final FileSet fileSet) {
        assert fileSet != null;

        List list = fileSet.getIncludes();

        if (list != null) {
            return new HashSet(list);
        }

        return Collections.singleton("**/*");
    }

    protected Set getExcludesFrom(final FileSet fileSet) {
        assert fileSet != null;

        List list = fileSet.getExcludes();

        if (list != null) {
            return new HashSet(list);
        }

        return Collections.EMPTY_SET;
    }

    protected File[] scanForSources(final FileSet fileSet, final SourceMapping[] mappings) throws Exception {
        assert fileSet != null;

        File sourceDir = new File(fileSet.getDirectory());
        log.debug("Scanning for sources in: {}", sourceDir);

        if (!sourceDir.exists()) {
            log.debug("Skipping; missing source directory: {}", sourceDir);
            
            return new File[0];
        }
        
        File targetDir = getOutputDirectory();
        Set includes = getIncludesFrom(fileSet);
        Set excludes = getExcludesFrom(fileSet);
        
        StaleResourceScanner scanner = new StaleResourceScanner(0, includes, excludes);
        
        if (mappings != null && mappings.length > 0) {
            for (int i=0; i<mappings.length; i++) {
                scanner.addSourceMapping(mappings[i]);
            }
        }

        Set files = scanner.getIncludedSources(sourceDir, targetDir);

        return (File[]) files.toArray(new File[files.size()]);
    }

    protected File[] scanForSources(final FileSet fileSet, final SourceMapping mapping) throws Exception {
        assert mapping != null;

        return scanForSources(fileSet, new SourceMapping[] { mapping }); 
    }
}
