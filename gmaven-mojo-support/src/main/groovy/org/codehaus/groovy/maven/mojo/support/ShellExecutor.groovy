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

package org.codehaus.groovy.maven.mojo.support

// import org.codehaus.groovy.maven.util.AntBuilder

/**
 * Helper to execute shell (/bin/sh) scripts.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class ShellExecutor
{
    private GroovyLog log = new GroovyLog(this)
    
    private AntBuilder ant = new AntBuilder()
    
    private List body = []
    
    boolean failOnError = false
    
    def ShellExecutor(String script) {
        body << '#!/bin/sh'
        if (script) {
            this << script
        }
    }
    
    def ShellExecutor() {
        this(null)
    }
    
    def leftShift(String text) {
        assert text
        
        // Split up the script into multi-line for better output handling
        new StringReader(text).eachLine { line ->
            body << line
        }
    }
    
    //
    // TODO: Complain if shell is missing, or on wrong platform?
    //
    
    //
    // TODO: Add BatchExecutor (ick) ?
    //
    
    //
    // TODO: Clean up output
    //
    
    def execute(List args) {
        def scriptFile = File.createTempFile('shell-executor', '.sh')
        scriptFile.deleteOnExit()
        
        scriptFile.withPrintWriter { writer ->
            body.each {
                writer.println(it.trim())
            }
        }
        
        log.info "Executing shell script: $scriptFile"
        
        println '----8<----'
        scriptFile.eachLine { line ->
            println line
        }
        println '---->8----'
        
        ant.chmod(perm: 'u+x', file: scriptFile)
        
        try {
            // Ant does not like to replace props, so make a unique one each time
            def propname = 'shell-executor.' + UUID.randomUUID()
            
            ant.exec(executable: scriptFile, failonerror: failOnError, outputproperty: propname) {
                if (args) {
                    args.each {
                        arg(value: it)
                    }
                }
            }
            
            def result = ant.antProject.properties[propname]
            
            log.info 'Shell output:'
            
            println '----8<----'
            println result
            println '---->8----'
            
            return result
        }
        finally {
            scriptFile.delete()
        }
    }
    
    def execute() {
        return execute(null)
    }
    
    static def execute(String script, List args) {
        return new ShellExecutor(script).execute(args)
    }
    
    static def execute(String script) {
        return execute(script, null)
    }
}

