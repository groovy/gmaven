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

package org.codehaus.groovy.maven.gossip;

import org.codehaus.groovy.maven.gossip.config.Configurator;
import org.codehaus.groovy.maven.gossip.model.EffectiveProfile;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Factory to produce {@link Logger} instances.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class Gossip
    implements ILoggerFactory
{
    private final InternalLogger log = InternalLogger.getLogger(getClass());

    private final Map loggers;

    private EffectiveProfile profile;

    private LoggerImpl root = new LoggerImpl("<ROOT>", Level.INFO);

    public Gossip() {
        loggers = new HashMap();

        profile = new Configurator().configure();

        prime();
    }

    private void prime() {
        log.trace("Priming");

        // Prime the loggers we have configured
        for (Iterator iter = profile.loggers().entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            String name = (String) entry.getKey();
            org.codehaus.groovy.maven.gossip.model.Logger node =
                    (org.codehaus.groovy.maven.gossip.model.Logger)entry.getValue();

            LoggerImpl logger = (LoggerImpl) getLogger(name);
            logger.level = node.getLevel();
        }
    }

    public Logger getLogger(final String name) {
        assert name != null;

        LoggerImpl logger;

        synchronized (loggers) {
            Object obj = loggers.get(name);

            if (obj == null) {
                logger = new LoggerImpl(name);

                loggers.put(name, logger);

                log.trace("Created logger: {}", logger);

                updateParents(logger);
            }
            else if (obj instanceof ProvisionNode) {
                logger = new LoggerImpl(name);

                loggers.put(name, logger);

                log.trace("Replaced provision node with logger: {}", logger);

                updateChildren((ProvisionNode)obj, logger);
                
                updateParents(logger);
            }
            else if (obj instanceof LoggerImpl) {
                logger = (LoggerImpl)obj;

                log.trace("Using cached logger: {}", logger);
            }
            else {
                throw new InternalError();
            }
        }

        return logger;
    }

    private class LoggerImpl
        extends AbstractLogger
    {
        private Level level;

        private Level cachedLevel;

        private LoggerImpl parent;

        private LoggerImpl(final String name, final Level level) {
            super(name);

            this.level = level;
        }

        private LoggerImpl(final String name) {
            this(name, null);
        }

        private Level findEffectiveLevel() {
            for (LoggerImpl logger = this; logger != null; logger=logger.parent) {
                if (logger.level != null) {
                    return logger.level;
                }
            }

            return null;
        }

        private Level getEffectiveLevel() {
            if (cachedLevel == null) {
                cachedLevel = findEffectiveLevel();
            }

            return cachedLevel;
        }

        protected boolean isEnabled(final Level level) {
            assert level != null;

            return getEffectiveLevel().id <= level.id;
        }

        protected void doLog(final Level level, final String message, final Throwable cause) {
            profile.filter(new Event(this, level, message, cause));
        }
    }

    //
    // NOTE: The following was borrowed and massaged from Log4j
    //

    private class ProvisionNode
        extends ArrayList
    {
        private ProvisionNode(final LoggerImpl logger) {
            assert logger != null;
            
            add(logger);
        }
    }

    private void updateParents(final LoggerImpl logger) {
        assert logger != null;

        String name = logger.getName();
        int length = name.length();

        log.trace("Update parents: {}", name);

        boolean parentFound = false;

        // if name = "w.x.y.z", loop thourgh "w.x.y", "w.x" and "w", but not "w.x.y.z"
        for (int i = name.lastIndexOf('.', length - 1); i >= 0; i = name.lastIndexOf('.', i - 1)) {
            String key = name.substring(0, i);

            Object obj = loggers.get(key);

            // Create a provision node for a future parent.
            if (obj == null) {
                ProvisionNode pn = new ProvisionNode(logger);

                loggers.put(key, pn);
            }
            else if (obj instanceof LoggerImpl) {
                parentFound = true;

                logger.parent = (LoggerImpl) obj;

                // no need to update the ancestors of the closest ancestor
                break;
            }
            else if (obj instanceof ProvisionNode) {
	            ((ProvisionNode) obj).add(logger);
            }
            else {
                throw new InternalError();
            }
        }

        // If we could not find any existing parents, then link with root.
        if (!parentFound) {
            logger.parent = root;
        }
    }

    private void updateChildren(final ProvisionNode pn, final LoggerImpl logger) {
        assert pn != null;
        assert logger != null;

        final int last = pn.size();

        for (int i = 0; i < last; i++) {
            LoggerImpl l = (LoggerImpl) pn.get(i);

            log.trace("Updating child {}", l.name);

            // Unless this child already points to a correct (lower) parent,
            // make cat.parent point to l.parent and l.parent to cat.
            if (!l.parent.name.startsWith(logger.name)) {
                logger.parent = l.parent;
                l.parent = logger;
            }
        }
    }
}
