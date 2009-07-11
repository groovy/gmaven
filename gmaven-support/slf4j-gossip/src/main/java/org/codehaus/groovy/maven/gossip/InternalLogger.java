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

import java.io.PrintStream;

/**
 * ???
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class InternalLogger
    extends AbstractLogger
{
    private static Level level = Level.WARN;

    static {
        String tmp = System.getProperty(InternalLogger.class.getName() + ".level");

        if (tmp != null) {
            level = Level.forLabel(tmp);
        }
    }
    
    public InternalLogger(final String name) {
        super(name);
    }

    public static InternalLogger getLogger(final String name) {
        assert name != null;

        return new InternalLogger(name);
    }
    
    public static InternalLogger getLogger(final Class type) {
        assert type != null;

        return new InternalLogger(type.getName());
    }

    protected boolean isEnabled(final Level l) {
        assert l != null;
        
        return level.id <= l.id;
    }

    protected void doLog(final Level level, final String message, final Throwable cause) {
        assert message != null;
        // cause may be null
        // level should have checked already

        final PrintStream out = System.out;

        synchronized (out) {
            out.print("[");
            out.print(level.label);
            out.print("] ");

            switch (level.id) {
                case Level.INFO_ID:
                case Level.WARN_ID:
                    out.print(" ");
            }

            // out.print(" INTERNAL.");
            out.print(this.getName());
            out.print(" - ");

            out.println(message);

            if (cause != null) {
                cause.printStackTrace(out);
            }

            out.flush();
        }
    }
}