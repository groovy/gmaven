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

package org.codehaus.groovy.maven.gossip.model;

import org.codehaus.groovy.maven.gossip.config.ConfigurationFactory;

import java.io.File;
import java.net.URL;

/**
 * Source node.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class Source
    extends AbstractNode
{
    //
    // TODO: Consider adding triggers to these...
    //       so folks can configure how/when they load better
    //
    
    private MergePolicy mergePolicy;
    
    public MergePolicy getMergePolicy() {
        return mergePolicy;
    }

    public void setMergePolicy(final MergePolicy policy) {
        this.mergePolicy = policy;
    }

    public abstract Configuration load() throws Exception;

    protected Configuration load(final URL url) throws Exception {
        assert url != null;

        log.trace("Loading configuration from: {}", url);

        ConfigurationFactory factory = new ConfigurationFactory();

        Configuration config = factory.create(url);

        if (config == null) {
            log.warn("Unable to load configuration: {}", url);
        }
        else {
            log.trace("Loaded configuration: {}", config);
        }

        return config;
    }

    protected Configuration load(final File file) throws Exception {
        assert file != null;

        log.trace("Loading: {}", file);

        if (!file.exists()) {
            log.debug("File does not exist; skipping: {}", file);
            return null;
        }

        return load(file.toURI().toURL());
    }
}