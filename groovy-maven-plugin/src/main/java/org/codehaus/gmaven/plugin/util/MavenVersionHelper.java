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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Maven version helper.
 *
 * @since 2.0
 */
@Component(role = MavenVersionHelper.class)
public class MavenVersionHelper
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Requirement
  private VersionHelper versionHelper;

  public MavenVersionHelper() {}

  @VisibleForTesting
  public MavenVersionHelper(final VersionHelper versionHelper) {
    this.versionHelper = checkNotNull(versionHelper);
  }

  @Nullable
  public Version detectVersion(final ClassLoader classLoader) {
    checkNotNull(classLoader);

    log.trace("Detecting Maven version; class-loader: {}", classLoader);

    // TODO: Sort out how compatible this logic is for newer/older versions of maven

    Properties props = readProperties(
        classLoader, "org.apache.maven.cli.MavenCli", "/org/apache/maven/messages/build.properties");
    String raw = props.getProperty("version");

    log.trace("Raw version: {}", raw);
    if (raw == null) {
      return null;
    }

    Version version = versionHelper.parseVersion(raw);
    log.trace("Version: {}", version);
    return version;
  }

  private Properties readProperties(final ClassLoader classLoader, final String className, final String resourceName) {
    log.trace("Reading properties; class-loader: {}, class-name: {}, resource-name: {}",
        classLoader, className, resourceName);

    Properties props = new Properties();
    try {
      Class type = classLoader.loadClass(className);
      log.trace("Type: {}", type);

      URL resource = type.getResource(resourceName);
      log.trace("Resource: {}", resource);

      if (resource != null) {
        InputStream stream = resource.openStream();
        try {
          props.load(stream);
        }
        finally {
          stream.close();
        }
      }
    }
    catch (ClassNotFoundException e) {
      log.trace("Failed to resolve class", e);
    }
    catch (IOException e) {
      log.trace("Failed to read properties", e);
    }

    return props;
  }
}
