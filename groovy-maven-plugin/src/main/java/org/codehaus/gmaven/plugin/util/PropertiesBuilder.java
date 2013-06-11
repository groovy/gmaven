/*
 * Copyright (c) 2007-2013, the original author or authors.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.codehaus.gmaven.plugin.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper to build merged Maven execution properties.
 *
 * @since 2.0
 */
@Component(role = PropertiesBuilder.class)
public class PropertiesBuilder
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  private MavenProject project;

  private MavenSession session;

  public PropertiesBuilder setProject(final @Nullable MavenProject project) {
    this.project = project;
    return this;
  }

  public PropertiesBuilder setSession(final @Nullable MavenSession session) {
    this.session = session;
    return this;
  }

  public Map<String, String> build() {
    Map<String, String> props = Maps.newHashMap();
    if (project != null) {
      props.putAll(Maps.fromProperties(project.getProperties()));
    }
    if (session != null) {
      props.putAll(Maps.fromProperties(session.getSystemProperties()));
      props.putAll(Maps.fromProperties(session.getUserProperties()));
    }

    if (log.isTraceEnabled()) {
      log.trace("Properties:");
      List<String> keys = Lists.newArrayList(props.keySet());
      Collections.sort(keys);
      for (String key : keys) {
        log.trace("  {}={}", key, props.get(key));
      }
    }

    return props;
  }
}
