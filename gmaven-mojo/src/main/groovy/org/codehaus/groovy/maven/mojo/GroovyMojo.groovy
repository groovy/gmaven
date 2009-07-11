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

package org.codehaus.groovy.maven.mojo

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException

/**
 * Provides support for Maven 2 plugins implemented in Groovy.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
abstract class GroovyMojo
    extends AbstractMojo
{
    private AntBuilder ant

    /**
     * Sanatize errors, stripping out Groovy internals.
     */
    protected boolean sanitizeErrors = true

    /**
     * Lazilly initialize the AntBuilder, so we can pick up the log impl correctly.
     */
    protected AntBuilder getAnt() {
        if (!ant) {
            ant = createAntBuilder()
        }
        return ant
    }

    private AntBuilder createAntBuilder() {
        AntBuilder ant = new AntBuilder()
        
        def logger = ant.antProject.buildListeners[0]
        
        logger.emacsMode = true
        
        return ant
    }
    
    protected def fail(final msg) {
        assert msg

        if (msg instanceof Throwable) {
            fail(msg.message, msg)
        }

        throw new MojoExecutionException("$msg")
    }

    protected def fail(final msg, Throwable cause) {
        assert msg
        assert cause

        //
        // FIXME: Need to get the component injected...
        //
        /*
        if (sanitizeErrors) {
            cause = StackTraceSanitizer.sanitize(cause, true)
        }
        */

        throw new MojoExecutionException("$msg", cause)
    }

    //
    // FIXME: Need to make sure this stuff works and add tests to keep it working, or nuke it...
    //
    
    //
    // AOP support for execute() to handle a slightly richer Mojo instance lifecycle
    //

    /**
     * Intercept calls to execute() and surround by beforeExecute() and afterExecute()
     */
    def invokeMethod(String name, Object args) {
        def result

        switch (name) {
            case 'execute':
                beforeExecute()

                try {
                    result = metaClass.invokeMethod(this, name, args)
                }
                finally {
                    afterExecute()
                }

                break

            default:
                // Else pass the call through to the metaclass for invocation
                result = metaClass.invokeMethod(this, name, args)
        }

        return result
    }

    protected void beforeExecute() {
        // Empty
    }

    protected void afterExecute() {
        // Empty
    }
}
