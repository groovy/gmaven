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
package org.codehaus.gmaven.adapter;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Configuration details for construction of a {@code groovy.lang.GroovyCodeSource} instance.
 *
 * @since 2.0
 */
public final class ClassSource
{
  public final URL url;

  public final File file;

  public final Inline inline;

  private ClassSource(final @Nullable URL url,
                      final @Nullable File file,
                      final @Nullable Inline inline)
  {
    this.url = url;
    this.file = file;
    this.inline = inline;
  }

  private ClassSource(final URL url) {
    this(url, null, null);
  }

  private ClassSource(final File file) {
    this(null, file, null);
  }

  private ClassSource(final Inline inline) {
    this(null, null, inline);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
        "url=" + url +
        ", file=" + file +
        ", inline=" + inline +
        '}';
  }

  @VisibleForTesting
  static final AtomicInteger scriptCounter = new AtomicInteger(0);

  /**
   * Inline Groovy source.
   */
  public static final class Inline
  {
    public final String name;

    public final String codeBase;

    public final Reader input;

    private Inline(final String source) {
      this.name = "script" + scriptCounter.incrementAndGet() + ".groovy";
      this.codeBase = "/groovy/script";
      this.input = new StringReader(source);
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
   * Create a class-source from the given input.
   */
  public static ClassSource create(final String source) {
    checkNotNull(source);

    // First try and parse the source as a URL
    try {
      return new ClassSource(new URL(source));
    }
    catch (MalformedURLException e) {
      // ignore
    }

    // Then as a File
    File file = new File(source);
    if (file.exists()) {
      return new ClassSource(file);
    }

    // Else it is an inline
    return new ClassSource(new Inline(source));
  }
}
