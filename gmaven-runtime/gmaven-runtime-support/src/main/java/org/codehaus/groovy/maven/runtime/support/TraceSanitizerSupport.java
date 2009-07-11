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

package org.codehaus.groovy.maven.runtime.support;

import org.codehaus.groovy.maven.feature.Configuration;
import org.codehaus.groovy.maven.feature.Feature;
import org.codehaus.groovy.maven.feature.support.ComponentSupport;
import org.codehaus.groovy.maven.runtime.TraceSanitizer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

//
// NOTE: Based on from org.codehaus.groovy.runtime.StackTraceUtils.
//

/**
 * Provides a default stack trace sanitizer, should work with most versions of Groovy.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class TraceSanitizerSupport
    extends ComponentSupport
    implements TraceSanitizer
{
    public static final String[] FILTERED_PREFIXES = {
        "groovy.",
        "org.codehaus.groovy.",
        "java.",
        "javax.",
        "sun.",
        "gjdk.groovy.",
    };

    public TraceSanitizerSupport(final Feature feature, final Configuration config) {
        super(feature, config);
    }

    public TraceSanitizerSupport(final Feature feature) {
        super(feature);
    }

    public boolean filter(final String className) {
        assert className != null;

        for (int i = 0; i < FILTERED_PREFIXES.length; i++) {
            if (className.startsWith(FILTERED_PREFIXES[i])) {
                return true;
            }
        }

        return false;
    }

    public boolean filter(final Class type) {
        assert type != null;

        return filter(type.getName());
    }

    public Throwable sanitize(final Throwable t, final boolean deep) {
        assert t != null;

        if (deep) {
            Throwable current = t;

            while (current.getCause() != null) {
                current = sanitize(current.getCause(), false);
            }
        }

        StackTraceElement[] trace = t.getStackTrace();

        List list = new ArrayList();

        for (int i = 0; i < trace.length; i++) {
            if (!filter(trace[i].getClassName())) {
                list.add(trace[i]);
            }
        }

        t.setStackTrace((StackTraceElement[]) list.toArray(new StackTraceElement[list.size()]));

        return t;
    }

    public Throwable sanitize(final Throwable t) {
        return sanitize(t, false);
    }

    public void print(final Throwable t, final PrintWriter out, final boolean deep) {
        assert t != null;
        assert out != null;

        StackTraceElement[] trace = sanitize(t, deep).getStackTrace();

        for (int i = 0; i < trace.length; i++) {
            synchronized (out) {
                //
                // TODO: See if we can just use the toString()... why this muck?
                //

                out.print("at ");
                out.print(trace[i].getClassName());
                out.print("(");
                out.print(trace[i].getMethodName());
                out.print(":");
                out.print(trace[i].getLineNumber());
                out.println(")");
            }
        }
    }

    public void print(final Throwable t, final PrintWriter out) {
        print(t, out, false);
    }

    public void print(final Throwable t) {
        print(t, new PrintWriter(System.err, true));
    }
}