package org.codehaus.gmaven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Support for mojo implementations.
 *
 * @since 2.0
 */
public abstract class MojoSupport
    extends AbstractMojo
{
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Sub-class should use {@link #doExecute} instead.
     */
    public synchronized void execute() throws MojoExecutionException, MojoFailureException {
        try {
            doExecute();
        }
        catch (Exception e) {
            // Wrap to avoid truncating the stack-trace
            if (e instanceof MojoExecutionException) {
                throw new MojoExecutionException(e.getMessage(), e);
            }
            else if (e instanceof MojoFailureException) {
                MojoFailureException x = new MojoFailureException(e.getMessage());
                x.initCause(e);
                throw x;
            }
            else {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }
    }

    protected abstract void doExecute() throws Exception;
}
