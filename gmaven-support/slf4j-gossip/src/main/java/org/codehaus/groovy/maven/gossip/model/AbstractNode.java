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

import org.codehaus.groovy.maven.gossip.InternalLogger;

import java.util.Iterator;
import java.util.Properties;

/**
 * Support for {@link Node} implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class AbstractNode
    implements Node
{
    protected transient InternalLogger log = InternalLogger.getLogger(getClass());

    private transient Node parent;

    public Node getParent() {
        return parent;
    }

    protected void setParent(final Node parent) {
        this.parent = parent;
    }

    protected void mergeProperties(final Properties source, final Properties target) {
        assert source != null;
        assert target != null;

        log.debug("Merging properties");

        for (Iterator iter = source.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();

            String value = source.getProperty(key);

            target.setProperty(key, value);
        }
    }
}