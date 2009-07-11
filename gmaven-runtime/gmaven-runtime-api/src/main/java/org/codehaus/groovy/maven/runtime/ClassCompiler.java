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

package org.codehaus.groovy.maven.runtime;

import org.codehaus.groovy.maven.feature.Component;

import java.net.URL;

/**
 * Groovy class compiler abstraction.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public interface ClassCompiler
    extends org.codehaus.groovy.maven.runtime.util.Compiler, Component
{
    String KEY = ClassCompiler.class.getName();

    void setClassPath(URL[] urls);

    URL[] getClassPath();

    interface Keys
    {
        String DEBUG = "debug";

        String VERBOSE = "verbose";

        String SOURCE_ENCODING = "sourceEncoding";

        String TOLERANCE = "tolerance";
        
        String TARGET_BYTECODE = "targetBytecode";

        String SCRIPT_BASE_CLASSNAME = "scriptBaseClassname";

        String DEFAULT_SCRIPT_EXTENSION = "defaultScriptExtension";
        
        String WARNING_LEVEL = "warningLevel";

        String[] ALL = {
            DEBUG,
            VERBOSE,
            DEBUG,
            SOURCE_ENCODING,
            TARGET_BYTECODE,
            TOLERANCE,
            SCRIPT_BASE_CLASSNAME,
            DEFAULT_SCRIPT_EXTENSION,
            WARNING_LEVEL,
        };
    }
}