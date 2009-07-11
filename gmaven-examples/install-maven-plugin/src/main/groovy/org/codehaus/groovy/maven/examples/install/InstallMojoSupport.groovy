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

import org.codehaus.groovy.maven.mojo.GroovyMojo

import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.installer.ArtifactInstaller
import org.apache.maven.artifact.repository.ArtifactRepository

import org.codehaus.plexus.digest.Digester

import java.security.NoSuchAlgorithmException

/**
 * Support for installation mojos.
 *
 * @version $Id$
 */
abstract class InstallMojoSupport
    extends org.codehaus.groovy.maven.mojo.GroovyMojo
{
    /**
     * Flag to create checksums(MD5, SHA1) or not.
     *
     * @parameter expression="${createChecksum}" default-value="false"
     */
    boolean createChecksum
    
    //
    // Components
    //
    
    /**
     * @component
     * @required
     * @readonly
     */
    ArtifactInstaller installer

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    ArtifactRepository localRepository

    /**
     * Digester for MD5.
     * 
     * @component role-hint="md5"
     */
    Digester md5Digester

    /**
     * Digester for SHA-1.
     * 
     * @component role-hint="sha1"
     */
    Digester sha1Digester

    //
    // Support
    //
    
    protected void installCheckSum(File file, boolean isPom) {
        installCheckSum(file, null, isPom)
    }

    protected void installCheckSum(File file, Artifact artifact, boolean isPom) {
        assert file
        assert artifact
        
        try {
            def destination
            if (isPom) {
                destination = file
            }
            else {
                def localPath = localRepository.pathOf(artifact)
                destination = new File(localRepository.basedir, localPath)
            }
            
            def install = { algo ->
                // Create the checksum value
                def checksum = getChecksum(file, algo)
                
                // Make sure the install path exists
                if (!destination.parentFile.exists()) {
                    destination.parentFile.mkdirs()
                }
                
                // Figure out what the suffix should be (strip off "-" and make lower)
                def suffix = (algo - '-').toLowerCase()
                
                // Install the checksum file
                def tmp = new File("${destination}.${suffix}")
                tmp.write(checksum)
            }
            
            log.debug("Installing checksum for: $destination")
            
            [ 'MD5', 'SHA-1' ].each {
                install(it)
            }
        }
        catch (Exception e) {
            fail('Failed to create or install checksum', e)
        }
    }

    protected String getChecksum(File file, String algo) {
        assert file
        assert algo
        
        switch (algo) {
            case 'MD5':
                return md5Digester.calc(file)
            
            case 'SHA-1':
                return sha1Digester.calc(file)
            
            default:
                throw new NoSuchAlgorithmException("No support for algorithm: $algo")
        }
    }
}
