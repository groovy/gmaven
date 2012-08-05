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
import org.sonatype.aether.util.version.GenericVersionScheme;
import org.sonatype.aether.version.Version;
import org.sonatype.aether.version.VersionConstraint;
import org.sonatype.aether.version.VersionScheme;

import java.lang.reflect.Method;
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

    private final VersionScheme versionScheme = new GenericVersionScheme();

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

        // Modern versions of Groovy expose the version via GroovySystem.getVersion()
        Version version = getVersion(realm, "groovy.lang.GroovySystem", "getVersion");
        if (version == null) {
            // Older versions of Groovy expose the version via InvokerHelper.getVersion()
            version = getVersion(realm, "org.codehaus.groovy.runtime.InvokerHelper", "getVersion");
        }
        if (version == null) {
            log.error("Unable to detect Groovy version");
            return;
        }

        log.info("Version: {}", version);

        log.info("Compatibility:");
        VersionConstraint _1_5_constraint = versionScheme.parseVersionConstraint("[1.5,1.6)");
        log.info("  1.5: {}", _1_5_constraint.containsVersion(version));

        VersionConstraint _1_6_constraint = versionScheme.parseVersionConstraint("[1.6,1.7)");
        log.info("  1.6: {}", _1_6_constraint.containsVersion(version));

        VersionConstraint _1_7_constraint = versionScheme.parseVersionConstraint("[1.7,1.8)");
        log.info("  1.7: {}", _1_7_constraint.containsVersion(version));

        VersionConstraint _1_8_constraint = versionScheme.parseVersionConstraint("[1.8,1.9)");
        log.info("  1.8: {}", _1_8_constraint.containsVersion(version));

        VersionConstraint _1_9_constraint = versionScheme.parseVersionConstraint("[1.9,2.0)");
        log.info("  1.9: {}", _1_9_constraint.containsVersion(version));

        VersionConstraint _2_0_constraint = versionScheme.parseVersionConstraint("[2.0,2.1)");
        log.info("  2.0: {}", _2_0_constraint.containsVersion(version));

        VersionConstraint _2_1_constraint = versionScheme.parseVersionConstraint("[2.1,2.2)");
        log.info("  2.1: {}", _2_1_constraint.containsVersion(version));
    }

    private Version getVersion(final ClassLoader classLoader, final String className, final String methodName) {
        try {
            Class type = classLoader.loadClass(className);
            Method method = type.getMethod(methodName);
            Object result = method.invoke(null);
            if (result != null) {
                return versionScheme.parseVersion(result.toString());
            }
        }
        catch (Throwable e) {
            log.warn("Unable to get version from: {}", className);
        }
        return null;
    }
}
