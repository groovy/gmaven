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

package org.codehaus.groovy.maven.runtime.util;

import java.io.File;
import java.net.URL;
import java.util.Collection;

/**
 * A common interface for compiler components.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public interface Compiler
{
    void setTargetDirectory(File dir);

    File getTargetDirectory();
    
    void add(URL source);

    void add(File source);

    Collection sources();

    int compile() throws Exception;

    // Result class ?

    // Listener intf ?
}