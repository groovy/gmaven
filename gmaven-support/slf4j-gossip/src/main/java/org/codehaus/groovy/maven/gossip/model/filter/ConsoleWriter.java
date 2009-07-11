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

package org.codehaus.groovy.maven.gossip.model.filter;

import org.codehaus.groovy.maven.gossip.Event;
import org.codehaus.groovy.maven.gossip.config.ConfigurationException;
import org.codehaus.groovy.maven.gossip.model.Filter;
import org.codehaus.groovy.maven.gossip.model.render.Renderer;
import org.codehaus.groovy.maven.gossip.model.render.SimpleRenderer;

import java.io.PrintStream;

/**
 * Writes events to console.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ConsoleWriter
    extends Filter
{
    public static final String OUT = "OUT";

    public static final String SYSOUT = "SYSOUT";

    public static final String SYSTEM_OUT = "SYSTEM.OUT";

    public static final String ERR = "ERR";

    public static final String SYSERR = "SYSERR";

    public static final String SYSTEM_ERR = "SYSTEM.ERR";

    private String name = SYSTEM_OUT;
    
    private transient PrintStream stream;

    public ConsoleWriter() {}

    public ConsoleWriter(final String name) {
        setName(name);
    }

    public String toString() {
        return "ConsoleWriter{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        assert name != null;

        this.name = name;
    }

    private PrintStream getStream() {
        if (stream == null) {
            // Parse the stream
            String tmp = name.toUpperCase();
            if (OUT.equals(tmp) || SYSOUT.equals(tmp) || SYSTEM_OUT.equals(tmp)) {
                stream = System.out;
            }
            else if (ERR.equals(tmp) || SYSERR.equals(tmp) || SYSTEM_ERR.equals(tmp)) {
                stream = System.err;
            }
            else {
                throw new ConfigurationException("Unknown stream name: " + name);
            }

        }

        return stream;
    }

    public Result filter(final Event event) {
        assert event != null;

        Renderer renderer = getRenderer();
        
        if (renderer == null) {
            log.debug("Renderer not set; using default");
            
            renderer = new SimpleRenderer();
            setRenderer(renderer);
        }

        String text = renderer.render(event);

        PrintStream out = getStream();
        synchronized (out) {
            out.print(text);
            out.flush();
        }

        return STOP;
    }
}