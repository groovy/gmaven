/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance doWith the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * doWithOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.maven.common;

import java.io.PrintStream;

/**
 * Contains a pair of {@link PrintStream}.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class StreamPair
{
    public final PrintStream out;
    
    public final PrintStream err;
    
    public final boolean combined;
    
    public StreamPair(final PrintStream out, final PrintStream err) {
        assert out != null;
        assert err != null;
        
        this.out = out;
        this.err = err;
        this.combined = (out == err);
    }
    
    public StreamPair(final PrintStream out) {
        assert out != null;
        
        this.out = out;
        this.err = out;
        this.combined = true;
    }
    
    public PrintStream get(final Type type) {
        assert type != null;
        
        switch (type.code) {
            case OUT_CODE:
                return out;
            
            case ERR_CODE:
                return err;
        }
        
        // Should never happen
        throw new InternalError();
    }

    public String toString() {
        if (combined) {
            return "StreamPair{ " + out + ", * }";
        }

        return "StreamPair{ " + out + ", " + err + " }";
    }

    public void flush() {
        out.flush();
        
        if (!combined) {
            err.flush();
        }
    }
    
    public void close() {
        out.close();
        
        if (!combined) {
            err.close();
        }
    }

    /**
     * Create a new pair as System is currently configured.
     */
    public static StreamPair system() {
        return new StreamPair(System.out, System.err);
    }

    /**
     * Install the given stream pair as the System streams.
     */
    public static void system(final StreamPair streams) {
        assert streams != null;

        System.setOut(streams.out);
        System.setErr(streams.err);
    }

    /**
     * The original System streams (as they were when this class loads).
     */
    public static final StreamPair SYSTEM = system();

    /**
     * Pseudo-enum for a stream type.
     */
    public static final class Type
    {
        public final String name;
        
        public final int code;
        
        private Type(final String name, final int code) {
            this.name = name;
            this.code = code;
        }
        
        public String toString() {
            return name;
        }
    }
    
    public static final Type OUT = new Type("OUT", 0);
    
    public static final String OUT_NAME = "OUT";
    
    public static final int OUT_CODE = 0;
    
    public static final Type ERR = new Type("ERR", 1);
    
    public static final String ERR_NAME = "ERR";
    
    public static final int ERR_CODE = 1;
}
