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

package org.codehaus.groovy.maven.runtime.support;

import org.codehaus.groovy.maven.feature.ComponentException;
import org.codehaus.groovy.maven.feature.Configuration;
import org.codehaus.groovy.maven.feature.Feature;
import org.codehaus.groovy.maven.feature.support.ComponentSupport;
import org.codehaus.groovy.maven.runtime.util.Compiler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Support for {@link Compiler} component implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class CompilerSupport
    extends ComponentSupport
    implements Compiler
{
    protected final Set sources = new HashSet();

    protected File targetDir;

    protected CompilerSupport(final Feature feature, final Configuration config) {
        super(feature, config);
    }

    protected CompilerSupport(final Feature feature) {
        super(feature);
    }

    public void setTargetDirectory(final File dir) {
        assert dir != null;

        this.targetDir = dir;
    }

    public File getTargetDirectory() {
        if (targetDir == null) {
            throw new IllegalStateException("Target directory not bound");
        }

        return targetDir;
    }

    public void add(final URL source) {
        assert source != null;

        sources.add(source);

        log.debug("Added: {}", source);
    }

    public void add(final File source) {
        assert source != null;

        try {
            add(source.toURI().toURL());
        }
        catch (MalformedURLException e) {
            throw new ComponentException("Failed to coerce File to URL: " + source, e);
        }
    }

    public Collection sources() {
        return Collections.unmodifiableCollection(sources);
    }
}