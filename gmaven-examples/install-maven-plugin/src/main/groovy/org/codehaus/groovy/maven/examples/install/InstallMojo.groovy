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

package org.codehaus.groovy.maven.examples.install

//
// NOTE: Ported to Groovy from the org.apache.maven.plugins:maven-install-plugin
//

import org.apache.maven.project.MavenProject
import org.apache.maven.project.artifact.ProjectArtifactMetadata

/**
 * Installs project artifacts in local repository.
 *
 * @goal install
 * @phase install
 *
 * @version $Id$
 */
public class InstallMojo
    extends InstallMojoSupport
{
    /**
     * Whether to update the metadata to make the artifact as release.
     *
     * @parameter expression="${updateReleaseInfo}" default-value="false"
     */
    boolean updateReleaseInfo
    
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project

    //
    // Mojo
    //
    
    void execute() {
        def pomFile = project.file
        def artifact = project.artifact
        
        if (updateReleaseInfo) {
            artifact.release = true
        }

        if (project.packaging == 'pom') {
            installer.install(pomFile, artifact, localRepository)
        }
        else {
            def metadata = new ProjectArtifactMetadata(artifact, pomFile)
            artifact.addMetadata(metadata)

            if (artifact.file && artifact.file.exists() && !artifact.file.isDirectory()) {
                installer.install(artifact.file, artifact, localRepository)

                if (createChecksum) {
                    def pom = new File(localRepository.basedir, localRepository.pathOfLocalRepositoryMetadata(metadata, localRepository))
                    
                    installCheckSum(pom, true)
                    installCheckSum(artifact.file, artifact, false)
                }
            }
            else {
                fail('The packaging for this project did not assign a file to the build artifact')
            }
        }
        
        // Install all attached artifacts
        project.attachedArtifacts.each { attached ->
            installer.install(attached.file, attached, localRepository)

            if (createChecksum) {
                installCheckSum(attached.file, attached, false)
            }
        }
    }
}
