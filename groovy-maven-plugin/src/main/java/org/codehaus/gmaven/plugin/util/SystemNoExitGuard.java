package org.codehaus.gmaven.plugin.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;

/**
 * Helper to guard {@link Callable} execution against use of {@link System#exit(int)} and replacement of system streams.
 *
 * @since 2.0
 */
public class SystemNoExitGuard
{
  /**
   * Runnable-like interface which allows Exceptions.
   */
  public static interface Task
  {
    void run() throws Exception;
  }

  public void run(final Task task) throws Exception {
    // capture system streams
    final InputStream systemIn = System.in;
    final PrintStream systemOut = System.out;
    final PrintStream systemErr = System.err;

    // replace security manager
    final SecurityManager sm = System.getSecurityManager();
    System.setSecurityManager(new NoExitSecurityManager(sm));

    try {
      task.run();
    }
    finally {
      // restore security manager and system streams
      System.setSecurityManager(sm);
      System.setIn(systemIn);
      System.setOut(systemOut);
      System.setErr(systemErr);
    }
  }
}