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
import org.codehaus.groovy.maven.plugin.ComponentMojoSupport;
import org.codehaus.groovy.maven.runtime.Console;
import org.codehaus.groovy.maven.runtime.loader.realm.RealmManager;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.net.URLClassLoader;
import java.util.List;

/**
 * Launches the Groovy GUI console.
 *
 * @goal console
 * @requiresProject false
 * @requiresDependencyResolution test
 * @since 1.0-beta-2
 * 
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ConsoleMojo
    extends ComponentMojoSupport
{
    /**
     * @component
     *
     * @noinspection UnusedDeclaration
     */
    private RealmManager realmManager;

    public ConsoleMojo() {
        super(Console.KEY);
    }

    protected List getProjectClasspathElements() throws DependencyResolutionRequiredException {
        return project.getTestClasspathElements();
    }
    
    protected void process(final Component component) throws Exception {
        assert component != null;

        Console console = (Console) component;

        ClassRealm realm = realmManager.createComponentRealm(provider(), createClassPath());

        console.execute(realm);

        realmManager.releaseComponentRealm(realm);
    }
}
