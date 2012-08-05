package org.codehaus.gmaven.plugin;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * Resolve mojo.
 *
 * @since 2.0
 */
@Mojo(name = "resolve")
public class ResolveMojo
    extends MojoSupport
{
    @Component
    private MavenProject project;

    @Component
    private RepositorySystem repositorySystem;

    @Parameter(property="repositorySystemSession") // @Component does not work here
    private RepositorySystemSession repositorySession;

    @Override
    protected void doExecute() throws Exception {
        String artifactId = "org.codehaus.groovy:groovy:2.0.1";

        ArtifactRequest request = new ArtifactRequest();
        request.setArtifact(new DefaultArtifact(artifactId));
        request.setRepositories(project.getRemotePluginRepositories());

        log.info("Resolving artifact: {}", artifactId);
        ArtifactResult result = repositorySystem.resolveArtifact(repositorySession, request);
        log.info("Resolution result: {}", result);
    }
}
