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

package org.slf4j.impl;

import org.codehaus.groovy.maven.gossip.Gossip;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

/**
 * Gossip logger binder for SLF4J.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class StaticLoggerBinder
    implements LoggerFactoryBinder
{
    public static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
    
    private final ILoggerFactory factory;

    public StaticLoggerBinder() {
        this.factory = new Gossip();
    }

    public ILoggerFactory getLoggerFactory() {
        return factory;
    }

    public String getLoggerFactoryClassStr() {
        return Gossip.class.getName();
    }
}