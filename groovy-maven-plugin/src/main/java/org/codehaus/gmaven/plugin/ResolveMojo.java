package org.codehaus.gmaven.plugin;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;
import org.sonatype.aether.version.Version;

import java.net.URL;
import java.util.List;

/**
 * Resolve mojo.
 *
 * @since 2.0
 */
@Mojo(name = "resolve")
public class ResolveMojo
    extends MojoSupport
{
    private static final String DEFAULT_GROOVY_VERSION = "2.0.1";

    @Parameter(property="groovy.version", defaultValue=DEFAULT_GROOVY_VERSION)
    private String groovyVersion;

    @Component
    private MavenProject project;

    @Component
    private RepositorySystem repositorySystem;

    @Parameter(property="repositorySystemSession") // @Component does not work here
    private RepositorySystemSession repositorySession;

    @Override
    protected void doExecute() throws Exception {
        String artifactId = "org.codehaus.groovy:groovy:" + groovyVersion;

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRepositories(project.getRemotePluginRepositories());
        collectRequest.setRoot(new Dependency(new DefaultArtifact(artifactId), JavaScopes.RUNTIME));

        List<ArtifactResult> artifacts = repositorySystem.resolveDependencies(repositorySession, collectRequest, null);
        // FIXME: Filter out optional dependencies

        ClassWorld world = new ClassWorld();
        ClassLoader parentCl = Thread.currentThread().getContextClassLoader();
        String realmId = ClassRealms.uniqueId();

        ClassRealm realm = world.newRealm(realmId, parentCl);

        // TODO: Sort out if we want to expose any Maven bits at all, if not, could likely make the parentCl an empty realm
        //ClassRealms.setStrategy(realm, new ParentFirstStrategy(realm));

        log.info("Class-path:");
        for (ArtifactResult artifact : artifacts) {
            log.info("  {}", artifact);
            URL url = artifact.getArtifact().getFile().toURI().toURL();
            log.info("    {}", url);
            realm.addURL(url);
        }

        VersionDetector versionDetector = new VersionDetector();
        String tmp = versionDetector.detectVersion(realm);
        if (tmp == null) {
            log.error("Unable to detect Groovy version");
            return;
        }
        Version version = versionDetector.parseVersion(tmp);
        log.debug("Version: {}", version);
    }
}
