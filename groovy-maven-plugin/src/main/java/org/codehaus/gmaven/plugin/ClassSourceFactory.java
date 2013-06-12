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

package org.codehaus.gmaven.plugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import org.codehaus.gmaven.adapter.ClassSource;
import org.codehaus.gmaven.adapter.ClassSource.Inline;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Creates {@link ClassSource} instances
 *
 * @since 2.0
 */
@Component(role = ClassSourceFactory.class)
public class ClassSourceFactory
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  private static final class ClassSourceImpl
      implements ClassSource
  {
    private final URL url;

    private final File file;

    private final Inline inline;

    private ClassSourceImpl(final @Nullable URL url,
                            final @Nullable File file,
                            final @Nullable Inline inline)
    {
      this.url = url;
      this.file = file;
      this.inline = inline;
    }

    public URL getUrl() {
      return url;
    }

    public File getFile() {
      return file;
    }

    public Inline getInline() {
      return inline;
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + "{" +
          "url=" + url +
          ", file=" + file +
          ", inline=" + inline +
          '}';
    }
  }

  @VisibleForTesting
  static final AtomicInteger scriptCounter = new AtomicInteger(0);

  private static final class InlineImpl
    implements Inline
  {
    private final String name;

    private final String codeBase;

    private final Reader input;

    private InlineImpl(final String source) {
      this.name = "script" + scriptCounter.incrementAndGet() + ".groovy";
      this.codeBase = "/groovy/script";
      this.input = new StringReader(source);
    }

    public String getName() {
      return name;
    }

    public String getCodeBase() {
      return codeBase;
    }

    public Reader getInput() {
      return input;
    }

    @Override
    public String toString() {
      return getClass().getSimpleName() + "{" +
          "name='" + name + '\'' +
          ", codeBase='" + codeBase + '\'' +
          ", input=" + input +
          '}';
    }
  }

  /**
   * Create a class-source from the given source.
   */
  public ClassSource create(final String source) {
    checkNotNull(source);

    String trimmed = source.trim();
    log.trace("Creating class-source from: {}", trimmed);

    // First try and parse the source as a URL
    try {
      return new ClassSourceImpl(new URL(trimmed), null, null);
    }
    catch (MalformedURLException e) {
      log.trace("Not a URL", e);
    }

    // FIXME: This is _slightly_ problematic as if you have a valid file ref
    // FIXME: but the file is missing then it will be treated as inline instead of as a file

    // Then as a File
    try {
      File file = new File(trimmed).getCanonicalFile();
      if (file.exists()) {
        return new ClassSourceImpl(null, file, null);
      }
    }
    catch (IOException e) {
      log.trace("Not a File", e);
    }

    // Else it is an inline
    return new ClassSourceImpl(null, null, new InlineImpl(source));
  }
}
