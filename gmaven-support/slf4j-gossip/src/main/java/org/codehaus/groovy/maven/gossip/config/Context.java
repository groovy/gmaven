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

package org.codehaus.groovy.maven.gossip.config;

import org.codehaus.groovy.maven.gossip.InternalLogger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Container for Gossip configuration details.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class Context
    implements Cloneable
{
    private final InternalLogger log = InternalLogger.getLogger(getClass());

    private Map store;

    private String prefix;

    private Context parent;

    public Context(final Map store, final String prefix) {
        assert store != null;
        // prefix can be null

        this.store = store;
        this.prefix = prefix;
    }

    public Context(final Map store) {
        this(store, null);
    }

    public Context() {
        this(new HashMap(), null);
    }

    public Context(final Context config) {
        this(config.store, config.prefix);
    }

    public String toString() {
        if (prefix == null) {
            return "Context[]";
        }

        return "Context[" + prefix + "]"; 
    }

    /** @noinspection CloneDoesntDeclareCloneNotSupportedException */
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    void dump() {
        dump("    ");
    }

    void dump(final String pad) {
        assert pad != null;

        for (Iterator iter = store.keySet().iterator(); iter.hasNext();) {
            String name = (String)iter.next();
            Object value = store.get(name);

            log.debug("{}{}={}", new Object[] { pad, name, value });
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public String key(final String name) {
        assert name != null;

        if (prefix != null) {
            return prefix + "." + name;
        }

        return name;
    }

    public boolean contains(final String name) {
        assert name != null;

        return store.containsKey(key(name));
    }

    public Object set(final String name, final Object value) {
        assert name != null;
        assert value != null;

        return store.put(key(name), value);
    }

    public Object get(final String name, final Object defaultValue) {
        assert name != null;
        // defaultValue can be null

        Object value = store.get(key(name));

        if (value == null) {
            value =  defaultValue;
        }

        return value;
    }

    public Object get(final String name) {
        return get(name, (Object)null);
    }

    public Object remove(final String name) {
        assert name != null;

        return store.remove(key(name));
    }

    public void clear() {
        store.clear();
    }

    public int size() {
        if (prefix == null) {
            return store.size();
        }

        int c = 0;

        for (Iterator iter=store.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            if (key.startsWith(prefix)) {
                c++;
            }
        }

        return c;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Set names() {
        if (prefix == null) {
            return Collections.unmodifiableSet(store.keySet());
        }
        
        Set matching = new HashSet();
        int l = prefix.length();

        for (Iterator iter=store.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();

            if (key.startsWith(prefix + ".")) {
                // Strip off the prefix
                key = key.substring(l + 1, key.length());

                matching.add(key);
            }
        }
        
        return Collections.unmodifiableSet(matching);
    }

    //
    // Children
    //

    public Context parent() {
        if (parent == null) {
            throw new IllegalStateException("Parent is not bound");
        }

        return parent;
    }

    public Context child(final String prefix) {
        assert prefix != null;

        Context child = (Context) clone();

        child.parent = this;

        if (child.prefix != null) {
            child.prefix += "." + prefix;
        }
        else {
            child.prefix = prefix;
        }

        return child;
    }

    //
    // Typed Access
    //

    public Object set(final String name, final boolean value) {
        return set(name, Boolean.valueOf(value));
    }

    public boolean get(final String name, final boolean defaultValue) {
        Object value = get(name);

        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue();
        }

        return Boolean.valueOf(String.valueOf(value)).booleanValue();
    }

    public Object set(final String name, final int value) {
        return set(name, new Integer(value));
    }

    public int get(final String name, final int defaultValue) {
        Object value = get(name);

        if (value == null) {
            return defaultValue;
        }

        if (value instanceof Number) {
            return ((Number)value).intValue();
        }

        return Integer.valueOf(String.valueOf(value)).intValue();
    }

    public String get(final String name, final String defaultValue) {
        Object value = get(name);

        if (value == null) {
            return defaultValue;
        }

        if (value instanceof String) {
            return ((String)value);
        }

        return String.valueOf(value);
    }

    public File get(final String name, final File defaultValue) {
        Object value = get(name);

        if (value == null) {
            return defaultValue;
        }

        if (value instanceof File) {
            return ((File)value);
        }

        return new File(String.valueOf(value));
    }

    public URL get(final String name, final URL defaultValue) {
        Object value = get(name);

        if (value == null) {
            return defaultValue;
        }

        if (value instanceof URL) {
            return ((URL)value);
        }

        try {
            return new URL(String.valueOf(value));
        }
        catch (MalformedURLException e) {
            throw new ConfigurationException("Unable to decode URL; name=" + name + ", value=" + value, e);
        }
    }

    public URI get(final String name, final URI defaultValue) {
        Object value = get(name);

        if (value == null) {
            return defaultValue;
        }

        if (value instanceof URI) {
            return ((URI)value);
        }

        try {
            return new URI(String.valueOf(value));
        }
        catch (URISyntaxException e) {
            throw new ConfigurationException("Unable to decode URI; name=" + name + ", value=" + value, e);
        }
    }
}