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

//
// TODO: Consider renaming... not really process specific
//

//
// TODO: Add unitests for this puppy!
//

/**
 * Helper to execute a process and perform some verification logic to determine if the process is up or not.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
class ProcessLauncher
{
    //
    // TODO: Use logging
    //
    
    String name
    
    Closure process
    
    Closure verifier
    
    int verifyWaitDelay = 1000
    
    int timeout = -1
    
    boolean background = false
    
    def launch() {
        assert process
        assert name
        
        //
        // FIXME: Can probably just use a Throwable local here
        //
        def errors = []
        
        def runner = {
            try {
                process()
            }
            catch (Exception e) {
                errors << e
            }
        }
        
        def t = new Thread(runner, "$name Runner")
        
        println "Launching $name"
        t.start()
        
        if (verifier) {
            def timer = new Timer("$name Timer", true)
            
            def timedOut = false
            
            def timeoutTask
            if (timeout > 0) {
                timeoutTask = timer.runAfter(timeout * 1000, {
                    timedOut = true
                })
            }
            
            def started = false
            
            println "Waiting for ${name}..."
            
            while (!started) {
                if (timedOut) {
                    throw new Exception("Unable to verify if $name was started in the given time ($timeout seconds)")
                }
                
                if (errors) {
                    throw new Exception("Failed to start: $name", errors[0])
                }
                
                if (verifier()) {
                    started = true
                }
                else {
                    Thread.sleep(verifyWaitDelay)
                }
            }
            
            timeoutTask?.cancel()
        }
        
        println "$name started"
        
        if (!background) {
            println "Waiting for $name to shutdown..."
            
            t.join()
            
            println "$name has shutdown"
        }
    }
}
