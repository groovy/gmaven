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

package org.codehaus.groovy.maven.runtime.v10;

import groovy.ui.InteractiveShell;
import org.codehaus.groovy.maven.feature.Component;
import org.codehaus.groovy.maven.feature.support.ComponentSupport;
import org.codehaus.groovy.maven.feature.support.FeatureSupport;
import org.codehaus.groovy.maven.runtime.Shell;
import org.codehaus.groovy.maven.runtime.support.util.NoExitSecurityManager;
import org.codehaus.groovy.maven.common.StreamPair;

/**
 * ???
 *
 * @plexus.component role="org.codehaus.groovy.maven.feature.Feature#1.0"
 *                   role-hint="org.codehaus.groovy.maven.runtime.Shell"
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ShellFeature
    extends FeatureSupport
{
    public ShellFeature() {
        super(Shell.KEY);
    }

    protected Component doCreate() throws Exception {
        return new ShellImpl();
    }

    private class ShellImpl
        extends ComponentSupport
        implements Shell
    {
        private ShellImpl() throws Exception {
            super(ShellFeature.this);
        }

        public void execute() throws Exception {
            final StreamPair streams = StreamPair.system();

            // Put a nice blank before and after we run the shell
            streams.out.println();

            SecurityManager sm = System.getSecurityManager();
            System.setSecurityManager(new NoExitSecurityManager());

            try {
                InteractiveShell shell = new InteractiveShell();
                shell.run(null);
            }
            finally {
                System.setSecurityManager(sm);

                StreamPair.system(streams);
            }

            // The blank after
            streams.out.println();
        }
    }
}