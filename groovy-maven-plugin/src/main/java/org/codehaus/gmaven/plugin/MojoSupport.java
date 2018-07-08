/*
 * Copyright (c) 2006-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.gmaven.plugin;

import com.google.common.base.Throwables;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support for {@link Mojo} implementations.
 *
 * @since 2.0
 */
public abstract class MojoSupport
    extends AbstractMojo
{
  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected MojoSupport() {
    log.trace("Construct");
  }

  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      try {
        log.trace("Prepare");
        prepare();

        log.trace("Run");
        run();
      }
      finally {
        log.trace("Cleanup");
        cleanup();
      }
    }
    catch (Exception e) {
      log.trace("Failure", e);
      Throwables.propagateIfPossible(e, MojoExecutionException.class, MojoFailureException.class);
      throw Throwables.propagate(e);
    }
  }

  protected void prepare() throws Exception {
    // empty
  }

  protected abstract void run() throws Exception;

  protected void cleanup() throws Exception {
    // empty
  }
}
