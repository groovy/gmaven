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

package org.codehaus.groovy.maven.runtime.support.stubgen.model;

import org.codehaus.groovy.maven.runtime.support.stubgen.parser.SourceType;

import java.net.URL;

/**
 * Provides factory access to create a {@link SourceDef} from a URL.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public interface ModelFactory
{
    SourceDef create(final URL input) throws Exception;

    SourceDef create(final URL input, final SourceType type) throws Exception;

    //
    // TODO: Consider adding inputstream and/or reader impl with sourcetype
    //
}