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

package org.codehaus.groovy.maven.examples.clean

import org.codehaus.groovy.maven.mojo.GroovyMojo

import org.apache.maven.project.MavenProject

import org.apache.maven.shared.model.fileset.FileSet
import org.apache.maven.shared.model.fileset.util.FileSetManager

/**
 * Cleans build generated output.
 *
 * @goal clean
 *
 * @version $Id$
 */
class CleanMojo
    extends GroovyMojo
{
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project
    
    /**
     * Extra files to be deleted in addition to the default directories.
     *
     * @parameter
     */
    FileSet[] filesets
    
    void execute() {
        ant.delete(dir: project.build.directory)
        ant.delete(dir: project.build.outputDirectory)
        ant.delete(dir: project.build.testOutputDirectory)
        ant.delete(dir: project.reporting.outputDirectory)
        
        // Then if given delete the additional files specified by the filesets
        if (filesets) {
            def fsm = new FileSetManager(log, verbose)
            
            filesets.each {
                fsm.delete(it)
            }
        }
    }
}
