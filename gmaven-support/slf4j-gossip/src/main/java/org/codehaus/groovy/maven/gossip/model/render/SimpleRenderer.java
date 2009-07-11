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

package org.codehaus.groovy.maven.gossip.model.render;

import org.codehaus.groovy.maven.gossip.Event;
import org.codehaus.groovy.maven.gossip.Level;

/**
 * A simple event renderer.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class SimpleRenderer
    extends Renderer
{
    private boolean includeName = false;

    private boolean shortName = false;

    private int nameWidth = -1;

    public SimpleRenderer() {}
    
    public String toString() {
        return "SimpleRenderer{" +
                "includeName=" + includeName +
                ", shortName=" + shortName +
                ", nameWidth=" + nameWidth +
                '}';
    }

    public boolean getIncludeName() {
        return includeName;
    }

    public void setIncludeName(final boolean flag) {
        this.includeName = flag;
    }

    public void setIncludeName(final String flag) {
        setIncludeName(Boolean.valueOf(flag).booleanValue());
    }

    public void setShortName(final boolean flag) {
        this.shortName = flag;
    }

    public void setShortName(final String flag) {
        setShortName(Boolean.valueOf(flag).booleanValue());
    }

    public void setNameWidth(final int width) {
        this.nameWidth = width;
    }

    public void setNameWidth(final String width) {
        setNameWidth(Integer.parseInt(width));
    }

    public String render(final Event event) {
        assert event != null;

        log.trace("Rendering: {}", event);
        
        StringBuffer buff = new StringBuffer();

        buff.append("[");
        buff.append(event.level.label);
        buff.append("]");

        switch (event.level.id) {
            case Level.INFO_ID:
            case Level.WARN_ID:
                buff.append(" ");
        }

        buff.append(" ");

        if (includeName) {
            String name = event.logger.getName();

            if (shortName) {
                int i = name.lastIndexOf(".");
                
                if (i != -1) {
                    name = name.substring(i + 1, name.length());
                }
            }

            if (nameWidth > 0) {
                name = rightPad(name, nameWidth, " ");
            }

            buff.append(name);
            
            buff.append("- ");
        }
        
        buff.append(event.message);
        
        buff.append(NEWLINE);

        if (event.cause != null) {
            buff.append(event.cause);
            buff.append(NEWLINE);

            StackTraceElement[] trace = event.cause.getStackTrace();
            for (int i=0; i<trace.length; i++ ) {
                buff.append("    ");
                buff.append(trace[i]);
                buff.append(NEWLINE);
            }
        }

        return buff.toString();
    }

    //
    // Helpers
    //

    public static String repeat(final String str, final int repeat) {
        StringBuffer buff = new StringBuffer(repeat * str.length());

        for (int i = 0; i < repeat; i++) {
            buff.append(str);
        }

        return buff.toString();
    }

    public static String rightPad(String str, int size, final String delim) {
        size = (size - str.length()) / delim.length();

        if (size > 0) {
            str += repeat(delim, size);
        }

        return str;
    }
}