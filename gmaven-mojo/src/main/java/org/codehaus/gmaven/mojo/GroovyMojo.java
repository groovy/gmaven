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

package org.codehaus.gmaven.mojo;

import groovy.lang.GroovyObjectSupport;
import groovy.util.AntBuilder;
import org.apache.maven.plugin.ContextEnabled;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.tools.ant.BuildLogger;

import java.util.Map;

/**
 * Provides support for Maven 2 plugins implemented in Groovy.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class GroovyMojo
    extends GroovyObjectSupport
    implements Mojo, ContextEnabled
{
    private AntBuilder ant;

    /**
     * Lazily initialize the AntBuilder, so we can pick up the log impl correctly.
     */
    private AntBuilder getAnt() {
        if (ant == null) {
            ant = new AntBuilder();
            BuildLogger logger = (BuildLogger) ant.getAntProject().getBuildListeners().get(0);
            logger.setEmacsMode(true);
        }
        return ant;
    }

    public Object getProperty(final String property) {
        if ("ant".equals(property)) {
            return getAnt();
        }
        return super.getProperty(property);
    }

    protected void fail(final Object msg) throws MojoExecutionException {
        if (msg instanceof Throwable) {
            Throwable cause = (Throwable)msg;
            fail(cause.getMessage(), cause);
        }

        throw new MojoExecutionException(String.valueOf(msg));
    }

    protected void fail(final Object msg, final Throwable cause) throws MojoExecutionException {
        throw new MojoExecutionException(String.valueOf(msg), cause);
    }

    //
    // Mojo
    //

    private Log log;

    private Map pluginContext;

    public void setLog(final Log log) {
        this.log = log;
    }

    public Log getLog() {
        if (log == null) {
            log = new SystemStreamLog();
        }

        return log;
    }

    public Map getPluginContext() {
        return pluginContext;
    }

    public void setPluginContext(final Map context) {
        this.pluginContext = context;
    }
}
