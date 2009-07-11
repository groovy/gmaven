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

package org.codehaus.groovy.maven.gossip.model.trigger;

import org.codehaus.groovy.maven.gossip.model.Trigger;

/**
 * Support for triggers which expect a name and value for configuration.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class NameValueTriggerSupport
    extends Trigger
{
    private String name;

    private String value;

    private Boolean trim;

    private Boolean ignoreCase;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Boolean getTrim() {
        return trim;
    }

    public void setTrim(final Boolean trim) {
        this.trim = trim;
    }

    public void setTrim(final String trim) {
        setTrim(Boolean.valueOf(trim));
    }

    public void setTrim(final boolean trim) {
        setTrim(Boolean.valueOf(trim));
    }

    public Boolean getIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(final Boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

    public void setIgnoreCase(final String ignoreCase) {
        setIgnoreCase(Boolean.valueOf(ignoreCase));
    }
    
    public void setIgnoreCase(final boolean ignoreCase) {
        setIgnoreCase(Boolean.valueOf(ignoreCase));
    }

    protected abstract String resolve();

    public boolean isActive() {
        assert name != null;
        // value can be null;

        String have = resolve();

        if (log.isTraceEnabled()) {
            log.trace("Checking active state; name={}, expect={}, found={}", new Object[] { name, value, have });
        }

        // If not set, its not active
        if (have == null) {
            return false;
        }

        // We are set, but no value, so set means active
        if (value == null) {
            return true;
        }

        String want = value;

        // Trim if asked
        if (trim != null && trim.equals(Boolean.TRUE)) {
            want = want.trim();
            have = have.trim();
        }

        // Else value needs to equal our value
        if (ignoreCase != null && ignoreCase.equals(Boolean.TRUE)) {
            return want.equalsIgnoreCase(have);
        }
        else {
            return want.equals(have);
        }
    }

    public String toString() {
        return "NameValueTriggerSupport{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", trim=" + trim +
                ", ignoreCase=" + ignoreCase +
                '}';
    }
}