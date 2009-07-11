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

package org.codehaus.groovy.maven.feature;

/**
 * Container for version information in the form of <tt>major.minor.revision-tag</tt>.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public final class Version
{
    public final int major;

    public final int minor;

    public final int revision;

    public final String tag;

    public Version(final int major, final int minor, final int revision, final String tag) {
        assert major > 0;
        assert minor >= 0;
        assert revision >= 0;
        // tag can be null

        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.tag = tag;
    }

    public Version(final int major, final int minor, final int revision) {
        this(major, minor, revision, null);
    }

    public Version(final int major, final int minor) {
        this(major, minor, 0);
    }

    public Version(final int major) {
        this(major, 0);
    }

    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Version version = (Version) obj;

        if (major != version.major) {
            return false;
        }
        else if (minor != version.minor) {
            return false;
        }
        else if (revision != version.revision) {
            return false;
        }
        else if (tag != null ? !tag.equals(version.tag) : version.tag != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;

        result = major;
        result = 31 * result + minor;
        result = 31 * result + revision;
        result = 31 * result + (tag != null ? tag.hashCode() : 0);

        return result;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();

        buff.append(major);

        if (minor != -1) {
            buff.append(".").append(minor);
        }
        if (revision != -1) {
            buff.append(".").append(revision);
        }
        if (tag != null) {
            buff.append("-").append(tag);
        }
        
        return buff.toString();
    }

    //
    // TODO: Add some comparison methods
    //
}