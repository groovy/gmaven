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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Helper to guard {@link Task} execution against use of {@link System#exit(int)} (and related)
 * and to restore system streams once the task has finished.
 *
 * @since 2.0
 */
public class SystemNoExitGuard
{
  private static final Logger log = LoggerFactory.getLogger(SystemNoExitGuard.class);

  /**
   * Runnable-like interface which allows Exceptions.
   */
  public interface Task
  {
    void run() throws Exception;
  }

  private static class TaskContext {
    private InputStream systemIn = System.in;
    private PrintStream systemOut = System.out;
    private PrintStream systemErr = System.err;

    private SecurityManager sm = System.getSecurityManager();
    private SecurityManager newSm = System.getSecurityManager();
    private int reentrancyCount;


    synchronized void saveContext() {
      if (reentrancyCount++ == 0) {
        // capture system streams
        systemIn = System.in;
        systemOut = System.out;
        systemErr = System.err;

        // replace security manager
        sm = System.getSecurityManager();
        newSm = new NoExitSecurityManager(sm);
        System.setSecurityManager(newSm);
      } else {
        checkContextUpdate();
      }
    }

    synchronized void restoreContext() {
      System.setIn(systemIn);
      System.setOut(systemOut);
      System.setErr(systemErr);

      if (--reentrancyCount == 0) {
        System.setSecurityManager(sm);
      } else {
        checkContextUpdate();
      }
    }

    /**
     * Check that security context was updated out of the groovy plugin.
     * Original system IO streams can be restored in that case. For security manager only warning can be generated.
     */
    private void checkContextUpdate() {
      if (systemIn != System.in) {
        log.warn("System.in was updated in between groovy plugins");
      }
      if (systemOut != System.out) {
        log.warn("System.out was updated in between groovy plugins");
      }
      if (systemErr != System.err) {
        log.warn("System.err was updated in between groovy plugins");
      }
      if (System.getSecurityManager() != newSm) {
        log.warn("SecurityManager was updated in between groovy plugins");
      }
    }

  }

  private static final TaskContext taskContext = new TaskContext();

  public void run(final Task task) throws Exception {
    taskContext.saveContext();
    try {
      task.run();
    }
    finally {
      // restore security manager and system streams
      taskContext.restoreContext();
    }
  }
}