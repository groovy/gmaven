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
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * Support for the Gossip {@link org.slf4j.Logger} implementation.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class AbstractLogger
    extends MarkerIgnoringBase
    implements org.slf4j.Logger
{
    public final String name;

    protected AbstractLogger(final String name) {
        assert name != null;

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return getClass().getName() + "[" + getName() + "]@" + System.identityHashCode(this);
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Logger that = (Logger) obj;

        return !(name != null ? !name.equals(that.getName()) : that.getName() != null);

    }

    protected abstract boolean isEnabled(final Level level);

    protected abstract void doLog(Level level, String message, Throwable cause);

    private void log(final Level level, final String msg, final Throwable cause) {
        if (isEnabled(level)) {
            doLog(level, msg, cause);
        }
    }

    private void log(final Level level, final String msg) {
        if (isEnabled(level)) {
            doLog(level, msg, null);
        }
    }

    private void log(final Level level, final String format, final Object arg) {
        if (isEnabled(level)) {
            if (arg instanceof Throwable) {
                doLog(level, MessageFormatter.format(format, arg), (Throwable) arg);
            }
            else {
                doLog(level, MessageFormatter.format(format, arg), null);
            }
        }
    }

    private void log(final Level level, final String format, final Object arg1, final Object arg2) {
        if (isEnabled(level)) {
            if (arg2 instanceof Throwable) {
                doLog(level, MessageFormatter.format(format, arg1, arg2), (Throwable) arg2);
            }
            else {
                doLog(level, MessageFormatter.format(format, arg1, arg2), null);
            }
        }
    }

    private void log(final Level level, final String format, final Object[] args) {
        if (isEnabled(level)) {
            if (args != null && args.length != 0 && args[args.length - 1] instanceof Throwable) {
                doLog(level, MessageFormatter.arrayFormat(format, args), (Throwable) args[args.length - 1]);
            }
            else {
                doLog(level, MessageFormatter.arrayFormat(format, args), null);
            }
        }
    }

    //
    // TRACE
    //

    public boolean isTraceEnabled() {
        return isEnabled(Level.TRACE);
    }

    public void trace(final String msg) {
        log(Level.TRACE, msg);
    }

    public void trace(final String format, final Object arg) {
        log(Level.TRACE, format, arg);
    }

    public void trace(final String format, final Object arg1, final Object arg2) {
        log(Level.TRACE, format, arg1, arg2);
    }

    public void trace(final String format, final Object[] args) {
        log(Level.TRACE, format, args);
    }

    public void trace(final String msg, final Throwable cause) {
        log(Level.TRACE, msg, cause);
    }

    //
    // DEBUG
    //

    public boolean isDebugEnabled() {
        return isEnabled(Level.DEBUG);
    }

    public void debug(final String msg) {
        log(Level.DEBUG, msg);
    }

    public void debug(final String format, final Object arg) {
        log(Level.DEBUG, format, arg);
    }

    public void debug(final String format, final Object arg1, final Object arg2) {
        log(Level.DEBUG, format, arg1, arg2);
    }

    public void debug(final String format, final Object[] args) {
        log(Level.DEBUG, format, args);
    }

    public void debug(final String msg, final Throwable cause) {
        log(Level.DEBUG, msg, cause);
    }

    //
    // INFO
    //

    public boolean isInfoEnabled() {
        return isEnabled(Level.INFO);
    }

    public void info(final String msg) {
        log(Level.INFO, msg);
    }

    public void info(final String format, final Object arg) {
        log(Level.INFO, format, arg);
    }

    public void info(final String format, final Object arg1, final Object arg2) {
        log(Level.INFO, format, arg1, arg2);
    }

    public void info(final String format, final Object[] args) {
        log(Level.INFO, format, args);
    }

    public void info(final String msg, final Throwable cause) {
        log(Level.INFO, msg, cause);
    }

    //
    // WARN
    //

    public boolean isWarnEnabled() {
        return isEnabled(Level.WARN);
    }

    public void warn(final String msg) {
        log(Level.WARN, msg);
    }

    public void warn(final String format, final Object arg) {
        log(Level.WARN, format, arg);
    }

    public void warn(final String format, final Object[] args) {
        log(Level.WARN, format, args);
    }

    public void warn(final String format, final Object arg1, final Object arg2) {
        log(Level.WARN, format, arg1, arg2);
    }

    public void warn(final String msg, final Throwable cause) {
        log(Level.WARN, msg, cause);
    }

    //
    // ERROR
    //

    public boolean isErrorEnabled() {
        return isEnabled(Level.ERROR);
    }

    public void error(final String msg) {
        log(Level.ERROR, msg);
    }

    public void error(final String format, final Object arg) {
        log(Level.ERROR, format, arg);
    }

    public void error(final String format, final Object arg1, final Object arg2) {
        log(Level.ERROR, format, arg1, arg2);
    }

    public void error(final String format, final Object[] args) {
        log(Level.ERROR, format, args);
    }

    public void error(final String msg, final Throwable cause) {
        log(Level.ERROR, msg, cause);
    }
}