package org.codehaus.gmaven.plugin;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.shared.model.fileset.FileSet;


/**
 * Add source directories to the Maven project.
 *
 * @phase initialize
 * @goal addSource
 * @executionStrategy always
 * @requiresDirectInvocation false
 *
 * @version $Id$
 * @author Keegan Witt
 * @since 1.5
 */
public class AddSourceMojo extends MojoSupport {

    /**
     * The default directory in which Groovy sources are found
     *
     * @parameter expression="${project.basedir}/src/main/groovy"
     * @required
     * @readonly
     */
    protected File defaultSourceDir;

    /**
     * The default directory in which Groovy test sources are found
     *
     * @parameter expression="${project.basedir}/src/test/groovy"
     * @required
     * @readonly
     */
    protected File defaultTestSourceDir;

    /**
     * Source files to be included.  If not specified, then the default will be used.
     *
     * @parameter
     */
    protected FileSet[] sources;

    /**
     * Source files to be included.  If not specified, then the default will be used.
     *
     * @parameter
     */
    protected FileSet[] testSources;

    public void doExecute() throws MojoExecutionException {
        if (sources != null && sources.length > 0) {
            for (int i = 0, sourcesLength = sources.length; i < sourcesLength; i++) {
                try {
                    String path = new File(sources[i].getDirectory()).getCanonicalPath();
                    addSourcePath(path);
                } catch (IOException e) {
                    log.error("{}", e);
                }
            }
        } else {
            try {
                addSourcePath(defaultSourceDir.getCanonicalPath());
            } catch (IOException e) {
                log.error("{}", e);
            }
        }
        if (testSources != null) {
            for (int i = 0, sourcesLength = testSources.length; i < sourcesLength; i++) {
                try {
                    String path = new File(testSources[i].getDirectory()).getCanonicalPath();
                    addTestSourcePath(path);
                } catch (IOException e) {
                    log.error("{}", e);
                }
            }
        } else {
            try {
                addTestSourcePath(defaultTestSourceDir.getCanonicalPath());
            } catch (IOException e) {
                log.error("{}", e);
            }
        }
    }

    protected void addSourcePath(String path) {
        if (!project.getCompileSourceRoots().contains(path)) {
            log.debug("Added Source directory: " + path);
            project.addCompileSourceRoot(path);
        }
    }

    protected void addTestSourcePath(String path) {
        if (!project.getTestCompileSourceRoots().contains(path)) {
            log.debug("Added Test Source directory: " + path);
            project.addTestCompileSourceRoot(path);
        }
    }

}
