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

package org.codehaus.groovy.maven.runtime.support.stubgen.parser;

import org.codehaus.groovy.maven.runtime.support.stubgen.UnexpectedNodeException;

/**
 * Support for {@link Node} implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class NodeSupport
    implements Node
{
    protected abstract int type();

    protected abstract Tokens tokens();

    protected abstract String name();

    protected abstract int childCount();

    public boolean isLeaf() {
        return childCount() == 0;
    }
    
    public String toString() {
        String name = name();
        String text = text();
        String position = line() + ":" + column();

        if (name.equals(text)) {
            return "Node[" + position + "," + type() + "," + name + "]";
        }
        else {
            return "Node[" + position + "," + type() + "," + name + "=" + text + "]";
        }
    }

    public void dump(final String pad) {
        // FIXME: Use logging
        System.out.println(pad + this);
        
        Node child = firstChild();
        if (child != null) {
            child.dump(pad + "    ");
        }

        Node sibling = nextSibling();
        if (sibling != null) {
            sibling.dump(pad);
        }
    }

    public void dump() {
        dump("");
        System.out.println();
    }

    public boolean is(final String name) {
        assert name != null;

        return type() == tokens().value(name);
    }

    public boolean is(final String[] names) {
        assert names != null;
        assert names.length != 0;

        for (int i=0; i<names.length; i++) {
            if (is(names[i])) {
                return true;
            }
        }

        return false;
    }

    public void ensure(final String name) throws UnexpectedNodeException {
        if (!is(name)) {
            throw new UnexpectedNodeException(this);
        }
    }

    public Node skip(final String name) {
        if (is(name)) {
            return nextSibling();
        }

        return this;
    }
}