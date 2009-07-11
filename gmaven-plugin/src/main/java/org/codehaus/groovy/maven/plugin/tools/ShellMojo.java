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

package org.codehaus.groovy.maven.plugin.tools;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.feature.Configuration;
import org.codehaus.groovy.maven.plugin.ComponentMojoSupport;
import org.codehaus.groovy.maven.runtime.Shell;
import org.codehaus.groovy.maven.runtime.loader.realm.RealmManager;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.net.URLClassLoader;
import java.util.List;

/**
 * Launches the Groovy Shell (aka. <tt>groovysh</tt>).
 *
 * @goal shell
 * @requiresProject false
 * @requiresDependencyResolution test
 * @since 1.0-beta-2
 * 
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ShellMojo
    extends ComponentMojoSupport
{
    /**
     * Enable the <em>legacy</em> shell.
     *
     * @parameter expression="${legacy}" default-value="false"
     *
     * @noinspection UnusedDeclaration
     */
    private boolean legacy;

    /**
     * Enable verbose output (aka. <tt>groovysh --verbose</tt>).
     *
     * @parameter expression="${verbose}" default-value="false"
     *
     * @noinspection UnusedDeclaration
     */
    private boolean verbose;

    /**
     * Enable debug output (aka. <tt>groovysh --debug</tt>).
     *
     * @parameter expression="${debug}" default-value="false"
     *
     * @noinspection UnusedDeclaration
     */
    private boolean debug;

    /**
     * Suppress superfluous output (aka. <tt>groovysh --quiet</tt>).
     *
     * @parameter expression="${quiet}" default-value="false"
     *
     * @noinspection UnusedDeclaration
     */
    private boolean quiet;

    /**
     * Enable or disable use of ANSI colors (aka. <tt>groovysh --color</tt>).  Normally auto-detected.
     *
     * @parameter expression="${color}"
     *
     * @noinspection UnusedDeclaration
     */
    private Boolean color;

    /**
     * Specify the terminal type to use (aka. <tt>groovysh --terminal</tt>).  Normally auto-detected.
     * Full class name or one of "unix", "win", "windows", "false", "off" or "none" expected.
     *
     * @parameter expression="${terminal}"
     *
     * @noinspection UnusedDeclaration
     */
    private String terminal;

    /**
     * Optional arguments to pass to the shell when executing.
     *
     * @parameter expression="${args}"
     *
     * @noinspection UnusedDeclaration
     */
    private String args;

    /**
     * @component
     *
     * @noinspection UnusedDeclaration
     */
    private RealmManager realmManager;

    public ShellMojo() {
        super(Shell.KEY);
    }

    protected List getProjectClasspathElements() throws DependencyResolutionRequiredException {
        return project.getTestClasspathElements();
    }
    
    protected void configure(final Configuration config) throws Exception {
        assert config != null;

        config.set(Shell.Keys.LEGACY, legacy);

        if (!legacy) {
            config.set(Shell.Keys.VERBOSE, verbose);
            config.set(Shell.Keys.DEBUG, debug);
            config.set(Shell.Keys.QUIET, quiet);
            config.set(Shell.Keys.COLOR, color);
            config.set(Shell.Keys.TERMINAL, terminal);
            config.set(Shell.Keys.ARGS, args);
        }
    }

    protected void process(final Component component) throws Exception {
        assert component != null;

        Shell shell = (Shell) component;

        ClassRealm realm = realmManager.createComponentRealm(provider(), createClassPath());

        shell.execute(realm);

        realmManager.releaseComponentRealm(realm);
    }
}
