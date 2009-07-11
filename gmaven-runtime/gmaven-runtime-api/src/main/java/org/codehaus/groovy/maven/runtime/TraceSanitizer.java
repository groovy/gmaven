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

import java.io.PrintWriter;

/**
 * Provides an abstraction to sanatize Groovy stack-traces.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public interface TraceSanitizer
    extends Component
{
    String KEY = TraceSanitizer.class.getName();

    boolean filter(String className);

    boolean filter(Class type);

    Throwable sanitize(Throwable t, boolean deep);

    Throwable sanitize(Throwable t);

    void print(Throwable t, PrintWriter out, boolean deep);

    void print(Throwable t, PrintWriter out);

    void print(Throwable t);
}