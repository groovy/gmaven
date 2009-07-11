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

package mymojo

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException

/**
 * This is my mojo
 *
 * @goal mymojo
 *
 * @version $Id$
 */
class MyMojo
    extends AbstractMojo
{
    /**
     * @parameter expression="${project}"
     */
    def project
    
    void execute() throws MojoExecutionException, MojoFailureException {
        println()
        println "Groovy baby!"
        
        assert project != null
        
        println()
        println "... I started to work my mojo, to counter their mojo; we got cross-mojulation, and their heads started exploding."
        println()
    }
}
