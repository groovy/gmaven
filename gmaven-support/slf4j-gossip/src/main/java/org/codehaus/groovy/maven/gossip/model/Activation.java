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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Activation node.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class Activation
    extends ContainerNode
{
    private List triggers;

    public List triggers() {
        if (triggers == null) {
            triggers = new ArrayList();
        }

        return triggers;
    }

    public void addTrigger(final Trigger node) {
        assert node != null;

        triggers().add(node);
        node.setParent(this);
    }

    public boolean isActive() {
        // No triggers means its not active
        if (triggers == null) {
            log.trace("No triggers found; profile is not active");
            
            return false;
        }

        log.trace("Checking for active triggers");

        for (Iterator iter = triggers().iterator(); iter.hasNext();) {
            Trigger trigger = (Trigger) iter.next();

            // If active, then stop now
            if (trigger.isActive()) {
                log.debug("Active trigger: {}", trigger);
                return true;
            }
        }

        return false;
    }
}