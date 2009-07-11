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

import org.codehaus.groovy.maven.gossip.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Effective profile node.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class EffectiveProfile
    extends ContainerNode
{
    private List profiles;

    public EffectiveProfile() {}

    public List profiles() {
        if (profiles == null) {
            profiles = new ArrayList();
        }

        return profiles;
    }

    public void addProfile(final Profile node) {
        assert node != null;

        profiles().add(node);
        node.setParent(this);
    }

    private Map loggers;

    public Map loggers() {
        if (loggers == null) {
            log.trace("Loading effective logger table");

            Map map = new HashMap();

            for (Iterator iter=profiles().iterator(); iter.hasNext();) {
                Profile profile = (Profile) iter.next();

                for (Iterator iter2=profile.loggers().iterator(); iter2.hasNext();) {
                    Logger node = (Logger) iter2.next();
                    map.put(node.getName(), node);
                }
            }

            loggers = map;
        }

        return loggers;
    }

    private FilterChain[] chains;

    public void filter(final Event event) {
        assert event != null;

        log.trace("Filtering event: {}", event);

        if (chains == null) {
            List list = new ArrayList();

            for (Iterator iter=profiles().iterator(); iter.hasNext();) {
                Profile profile = (Profile) iter.next();
                list.add(profile.filters());
            }

            chains = (FilterChain[]) list.toArray(new FilterChain[list.size()]);
        }

        for (int i=0; i<chains.length; i++) {
            chains[i].filter(event);
        }
    }
}