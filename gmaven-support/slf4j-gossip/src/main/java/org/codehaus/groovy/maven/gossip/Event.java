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

import org.slf4j.Logger;

/**
 * Gossip logging event container.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class Event
{
    public final Logger logger;

    public final Level level;

    public final String message;

    public final Throwable cause;

    public Event(final Logger logger, final Level level, final String message, final Throwable cause) {
        this.logger = logger;
        this.level = level;
        this.message = message;
        this.cause = cause;
    }

    public String toString() {
        return getClass().getName() + "[" +
                logger.getName() +
                "/" +  level +
                "/" + message +
                (cause != null ? ("/" + cause) : "")
                + "]@" + System.identityHashCode(this);
    }

    //
    // TODO: Add some kinda render context?  To allow color filters to attach something so stream filters can render
    //
}