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
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.MapBasedValueSource;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
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

  private Map<String, String> properties;

  private Map<String, String> defaults;

  public PropertiesBuilder setProject(final @Nullable MavenProject project) {
    this.project = project;
    return this;
  }

  public PropertiesBuilder setSession(final @Nullable MavenSession session) {
    this.session = session;
    return this;
  }

  public PropertiesBuilder setProperties(final @Nullable Map<String, String> properties) {
    this.properties = properties;
    return this;
  }

  public PropertiesBuilder setDefaults(final @Nullable Map<String, String> defaults) {
    this.defaults = defaults;
    return this;
  }

  public Map<String, String> build() {
    Map<String, String> props = Maps.newHashMap();
    if (defaults != null) {
      props.putAll(defaults);
    }
    if (project != null) {
      props.putAll(Maps.fromProperties(project.getProperties()));
    }
    if (session != null) {
      props.putAll(Maps.fromProperties(session.getSystemProperties()));
      props.putAll(Maps.fromProperties(session.getUserProperties()));
    }
    if (properties != null) {
      props.putAll(properties);
    }

    // resolve any dangling references which could exist due to custom properties/defaults
    props = resolve(props);

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

  private Map<String, String> resolve(final Map<String, String> source) {
    Map<String, String> result = Maps.newHashMapWithExpectedSize(source.size());
    Interpolator interpolator = new StringSearchInterpolator();
    interpolator.addValueSource(new MapBasedValueSource(source));

    for (Entry<String, String> entry : source.entrySet()) {
      try {
        String value = interpolator.interpolate(entry.getValue());
        result.put(entry.getKey(), value);
      }
      catch (InterpolationException e) {
        log.warn("Failed to interpolate: {}={}", entry.getKey(), entry.getValue());
      }
    }

    return result;
  }
}
