package org.codehaus.gmaven.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.sonatype.aether.version.Version;

import java.net.URL;
import java.util.Set;

/**
 * Test mojo.
 *
 * @since 2.0
 */
@Mojo(name = "test", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class TestMojo
    extends MojoSupport
{
    @Component
    private MavenProject project;

    @Override
    protected void doExecute() throws Exception {
        Set<Artifact> artifacts = project.getArtifacts();

        ClassWorld world = new ClassWorld();
        ClassLoader parentCl = Thread.currentThread().getContextClassLoader();
        String realmId = ClassRealms.uniqueId();

        ClassRealm realm = world.newRealm(realmId, parentCl);

        // TODO: Sort out if we want to expose any Maven bits at all, if not, could likely make the parentCl an empty realm
        //ClassRealms.setStrategy(realm, new ParentFirstStrategy(realm));

        log.info("Class-path:");
        for (Artifact artifact : artifacts) {
            log.info("  {}", artifact);
            URL url = artifact.getFile().toURI().toURL();
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
