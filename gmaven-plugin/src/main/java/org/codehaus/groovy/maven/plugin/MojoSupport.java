/*
 * Copyright (C) 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.maven.plugin;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.groovy.maven.common.ArtifactItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Support for Mojo implementations.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class MojoSupport
    extends AbstractMojo
{
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     *
     * @noinspection UnusedDeclaration
     */
    protected MavenProject project;

    /**
     * Main Mojo execution hook.  Sub-class should use {@link #doExecute} instead.
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            doExecute();
        }
        catch (Exception e) {
            //
            // NOTE: Wrap to avoid truncating the stacktrace
            //

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

    //
    // Classpath Muck
    //

    protected List getProjectClasspathElements() throws DependencyResolutionRequiredException {
        return Collections.EMPTY_LIST;
    }

    protected ArtifactItem[] getUserClassspathElements() {
        return new ArtifactItem[0];
    }

    protected URL[] createClassPath() throws Exception {
        List list = new ArrayList();

        // Add the projects dependencies
        List files = getProjectClasspathElements();
        if (files != null) {
            log.debug("Project Classpath:");

            for (int i = 0; i < files.size(); ++i) {
                URL url = new File((String)files.get(i)).toURI().toURL();
                list.add(url);
                log.debug("    {}", url);
            }
        }

        // Add user dependencies (if there are any)
        ArtifactItem[] items = getUserClassspathElements();

        if (items != null) {
            log.debug("User Classpath:");

            for (int i=0; i < items.length; i++) {
                Artifact artifact = getArtifact(items[i]);
                URL url = artifact.getFile().toURI().toURL();
                list.add(url);
                log.debug("    {}", url);
            }
        }

        return (URL[])list.toArray(new URL[list.size()]);
    }

    //
    // Artifact Muck
    //

    /**
     * @component
     * @readonly
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    protected ArtifactFactory artifactFactory;

    /**
     * @component
     * @readonly
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    protected ArtifactResolver artifactResolver;

    /**
     * @component
     * @readonly
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    protected ArtifactMetadataSource artifactMetadataSource;

    /**
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    protected ArtifactRepository artifactRepository;
    
    /**
     * @parameter expression="${project.pluginArtifactRepositories}"
     * @required
     * @readonly
     *
     * @noinspection UnusedDeclaration
     */
    protected List remoteRepositories;
    
    /**
     * Create a new artifact. If no version is specified, it will be retrieved from the dependency
     * list or from the DependencyManagement section of the pom.
     *
     * @param item  The item to create an artifact for
     * @return      An unresolved artifact for the given item.
     *
     * @throws MojoExecutionException   Failed to create artifact
     */
    protected Artifact createArtifact(final ArtifactItem item) throws MojoExecutionException {
        assert item != null;

        if (item.getVersion() == null) {
            fillMissingArtifactVersion(item);

            if (item.getVersion() == null) {
                throw new MojoExecutionException("Unable to find artifact version of " + item.getGroupId()
                    + ":" + item.getArtifactId() + " in either dependency list or in project's dependency management.");
            }
        }

        // Convert the string version to a range
        VersionRange range;
        try {
            range = VersionRange.createFromVersionSpec(item.getVersion());
            log.trace("Using version range: {}", range);
        }
        catch (InvalidVersionSpecificationException e) {
            throw new MojoExecutionException("Could not create range for version: " + item.getVersion(), e);
        }

        return artifactFactory.createDependencyArtifact(
            item.getGroupId(),
            item.getArtifactId(),
            range,
            item.getType(),
            item.getClassifier(),
            Artifact.SCOPE_PROVIDED);
    }

    /**
     * Resolves the Artifact from the remote repository if nessessary. If no version is specified, it will
     * be retrieved from the dependency list or from the DependencyManagement section of the pom.
     *
     *
     * @param item  The item to create an artifact for; must not be null
     * @return      The artifact for the given item
     *
     * @throws MojoExecutionException   Failed to create artifact
     */
    protected Artifact getArtifact(final ArtifactItem item) throws MojoExecutionException {
        assert item != null;

        Artifact artifact = createArtifact(item);

        return resolveArtifact(artifact, false);
    }

    /**
     * Resolves the Artifact from the remote repository if nessessary. If no version is specified, it will
     * be retrieved from the dependency list or from the DependencyManagement section of the pom.
     *
     * @param artifact      The artifact to be resolved; must not be null
     * @param transitive    True to resolve the artifact transitivly
     * @return              The resolved artifact; never null
     *
     * @throws MojoExecutionException   Failed to resolve artifact
     */
    protected Artifact resolveArtifact(final Artifact artifact, final boolean transitive) throws MojoExecutionException {
        assert artifact != null;

        try {
            if (transitive) {
                artifactResolver.resolveTransitively(
                        Collections.singleton(artifact),
                        project.getArtifact(),
                        project.getRemoteArtifactRepositories(),
                        artifactRepository,
                        artifactMetadataSource);
            }
            else {
                artifactResolver.resolve(
                        artifact,
                        project.getRemoteArtifactRepositories(),
                        artifactRepository);
            }
        }
        catch (ArtifactResolutionException e) {
            throw new MojoExecutionException("Unable to resolve artifact", e);
        }
        catch (ArtifactNotFoundException e) {
            throw new MojoExecutionException("Unable to find artifact", e);
        }

        return artifact;
    }

    /**
     * Tries to find missing version from dependancy list and dependency management.
     * If found, the artifact is updated with the correct version.
     *
     * @param item  The item to fill in missing version details into
     */
    private void fillMissingArtifactVersion(final ArtifactItem item) {
        log.trace("Attempting to find missing version in {}:{}", item.getGroupId() , item.getArtifactId());

        List list = project.getDependencies();

        for (int i = 0; i < list.size(); ++i) {
            Dependency dependency = (Dependency) list.get(i);

            if (dependency.getGroupId().equals(item.getGroupId())
                && dependency.getArtifactId().equals(item.getArtifactId())
                && dependency.getType().equals(item.getType()))
            {
                log.trace("Found missing version: {} in dependency list", dependency.getVersion());

                item.setVersion(dependency.getVersion());

                return;
            }
        }

        list = project.getDependencyManagement().getDependencies();

        for (int i = 0; i < list.size(); i++) {
            Dependency dependency = (Dependency) list.get(i);

            if (dependency.getGroupId().equals(item.getGroupId())
                && dependency.getArtifactId().equals(item.getArtifactId())
                && dependency.getType().equals(item.getType()))
            {
                log.trace("Found missing version: {} in dependency management list", dependency.getVersion());

                item.setVersion(dependency.getVersion());
            }
        }
    }
}