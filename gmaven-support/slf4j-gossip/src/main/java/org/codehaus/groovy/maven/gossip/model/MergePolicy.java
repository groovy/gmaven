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

package org.codehaus.groovy.maven.gossip.model;

import org.codehaus.groovy.maven.gossip.Level;

/**
 * Merge-policy node.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class MergePolicy
{
    public static final int OVERWRITE_ID = 0;

    public static final MergePolicy OVERWRITE = new MergePolicy("OVERWRITE", OVERWRITE_ID);

    public static final MergePolicy DEFAULT = OVERWRITE;

    private static final MergePolicy[] ALL = {
        OVERWRITE,
    };

    public final String name;

    public final int id;

    private MergePolicy(final String name, final int id) {
        assert name != null;

        this.name = name;
        this.id = id;
    }

    public String toString() {
        return name;
    }

    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Level level = (Level) obj;

        return id == level.id;
    }

    public int hashCode() {
        return id;
    }

    //
    // Conversion
    //

    public static MergePolicy forName(final String name) {
        assert name != null;

        String tmp = name.trim().toUpperCase();

        for (int i=0; i< ALL.length; i++) {
            if (ALL[i].name.equals(tmp)) {
                return ALL[i];
            }
        }

        throw new IllegalArgumentException("Unknown merge policy: " + name);
    }

    public static MergePolicy forId(final int id) {
        for (int i=0; i< ALL.length; i++) {
            if (ALL[i].id == id) {
                return ALL[i];
            }
        }

        throw new IllegalArgumentException("Unknown merge policy id: " + id);
    }
}