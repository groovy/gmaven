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

package org.codehaus.groovy.maven.plugin;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Support for communication between stub generation and compilation.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class CompileState
{
    private Set forceCompile = new HashSet();

    private Set forceCompileTest = new HashSet();

    public void addForcedCompilationSource(final File file) {
        forceCompile.add(file);
    }

    public Set getForcedCompilationSources(final boolean clear) {
        Set set = Collections.unmodifiableSet(new HashSet(forceCompile));

        if (clear) {
            forceCompile.clear();
        }

        return set;
    }

    public void addForcedCompilationTestSource(final File file) {
        forceCompileTest.add(file);
    }

    public Set getForcedCompilationTestSources(final boolean clear) {
        Set set = Collections.unmodifiableSet(new HashSet(forceCompileTest));

        if (clear) {
            forceCompileTest.clear();
        }

        return set;
    }
}
