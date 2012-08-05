package org.codehaus.gmaven.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.util.version.GenericVersionScheme;
import org.sonatype.aether.version.InvalidVersionSpecificationException;
import org.sonatype.aether.version.Version;
import org.sonatype.aether.version.VersionConstraint;
import org.sonatype.aether.version.VersionScheme;

import java.lang.reflect.Method;

/**
 * Groovy version detector.
 *
 * @since 2.0
 */
public class VersionDetector
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final VersionScheme versionScheme = new GenericVersionScheme();

    public String detectVersion(final ClassLoader classLoader) {
        // Modern versions of Groovy expose the version via GroovySystem.getVersion()
        String version = getVersion(classLoader, "groovy.lang.GroovySystem", "getVersion");
        if (version == null) {
            // Older versions of Groovy expose the version via InvokerHelper.getVersion()
            version = getVersion(classLoader, "org.codehaus.groovy.runtime.InvokerHelper", "getVersion");
        }
        return version;
    }

    private String getVersion(final ClassLoader classLoader, final String className, final String methodName) {
        try {
            Class type = classLoader.loadClass(className);
            Method method = type.getMethod(methodName);
            Object result = method.invoke(null);
            if (result != null) {
                return result.toString();
            }
        }
        catch (Throwable e) {
            log.warn("Unable determine version from: {}", className);
        }
        return null;
    }

    public Version parseVersion(final String input) throws InvalidVersionSpecificationException {
        log.info("Parsing version: {}", input);
        Version version = versionScheme.parseVersion(input);

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

        return version;
    }
}
