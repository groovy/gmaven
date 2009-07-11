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

package org.codehaus.groovy.maven.gossip.model.source;

import org.codehaus.groovy.maven.gossip.config.MissingPropertyException;
import org.codehaus.groovy.maven.gossip.model.Configuration;
import org.codehaus.groovy.maven.gossip.model.Source;

import java.io.File;

/**
 * Home-directory configuration source.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class HomeDirectorySource
    extends Source
{
    private String path;
    
    public HomeDirectorySource() {}

    public HomeDirectorySource(final String path) {
        setPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public Configuration load() throws Exception {
        if (path == null) {
            throw new MissingPropertyException("path");
        }

        File homeDir = new File(System.getProperty("user.home"));

        File file = new File(homeDir, getPath());

        return load(file);
    }

    public String toString() {
        return "HomeDirectorySource{" +
                "path='" + path + '\'' +
                '}';
    }
}