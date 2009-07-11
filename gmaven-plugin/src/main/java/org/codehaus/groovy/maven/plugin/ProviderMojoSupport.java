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

import java.util.Collections;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.VersionRange;
import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.ProviderException;
import org.codehaus.groovy.maven.feature.ProviderLoader;
import org.codehaus.groovy.maven.feature.ProviderManager;
import org.codehaus.groovy.maven.runtime.loader.artifact.ArtifactHandler;
import org.codehaus.groovy.maven.runtime.loader.artifact.ArtifactProviderLoader;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

/**
 * Provides support for Mojo implementations which need to have access to a {@link Provider} instances.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public abstract class ProviderMojoSupport
    extends MojoSupport
    implements Contextualizable, Initializable
{
    /**
     * @component
     * @required
     * @readonly
     *
     * @noinspection UnusedDeclaration
     */
    private ProviderManager providerManager;

    /**
     * A comma-seperated list of provider keys, in order of preference of selection.
     *
     * @parameter expression="${groovy.runtime.provider}" default-value="default"
     *
     * @noinspection UnusedDeclaration
     */
    private String providerSelection;

    private PlexusContainer container;

    public void contextualize(final Context context) throws ContextException {
        assert context != null;

        container = (PlexusContainer) context.get(PlexusConstants.PLEXUS_KEY);
    }

    public void initialize() throws InitializationException {
        //
        // NOTE: For some horrible reason unknown to me, we can't configure this here... :-(
        //
        // configureArtifactProviderLoader();
    }

    private ArtifactHandler artifactHandler;

    private void configureArtifactProviderLoader() {
        if (artifactHandler == null) {
            try {
                ArtifactProviderLoader loader = (ArtifactProviderLoader) container.lookup(ProviderLoader.class.getName(), "artifact");
                loader.setHandler(new ArtifactHandlerImpl());
                artifactHandler = loader.getHandler();

                log.debug("Artifact loader configured with handler: {}", artifactHandler);
            }
            catch (Throwable t) {
                log.error("Failed to configure the artifact loader: " + t, t);
            }
        }
    }

    protected ProviderManager getProviderManager() {
        configureArtifactProviderLoader();

        return providerManager;
    }

    protected String getProviderSelection() {
        return providerSelection;
    }

    private Provider selectedProvider;

    protected Provider provider() throws Exception {
        if (selectedProvider == null) {
            selectedProvider = getProviderManager().select(getProviderSelection());
        }
        
        return selectedProvider;
    }

    /**
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     *
     * @noinspection UnusedDeclaration,MismatchedQueryAndUpdateOfCollection
     */
    protected Map pluginArtifactMap;

    //
    // ArtifactHandlerImpl
    //
    
    private class ArtifactHandlerImpl
        implements ArtifactHandler
    {
        // private final Logger log = LoggerFactory.getLogger(getClass());

        private final Artifact base;

        private final Artifact template;

        private final Artifact originating;

        public ArtifactHandlerImpl() throws Exception {
            String id = "org.codehaus.groovy.maven.runtime:gmaven-runtime-loader";

            this.base = (Artifact) pluginArtifactMap.get(id);

            if (base == null) {
                throw new ProviderException("Missing dependency in the list of plugin artifacts: " + id);
            }

            this.template = artifactFactory.createArtifact("org.codehaus.groovy.maven.runtime", "gmaven-runtime-", base.getBaseVersion(), base.getScope(), base.getType());

            this.originating = artifactFactory.createBuildArtifact("dummy", "dummy", "1.0", "jar");
        }

        public Artifact createQuery(final String key) {
            assert key != null;

            return artifactFactory.createArtifact(
                    template.getGroupId(),
                    template.getArtifactId() + key,
                    template.getVersion(),
                    template.getScope(),
                    template.getType());
        }

        public Artifact createDependency(final Artifact query) {
            assert query != null;

            return artifactFactory.createDependencyArtifact(
                    query.getGroupId(),
                    query.getArtifactId(),
                    VersionRange.createFromVersion(query.getVersion()),
                    "jar",
                    null,
                    Artifact.SCOPE_RUNTIME);
        }

        public ArtifactResolutionResult resolve(final Artifact artifact, final ArtifactFilter filter) throws ArtifactNotFoundException, ArtifactResolutionException {
            assert artifact != null;

            ArtifactFilter filters = new ArtifactFilter() {
                public boolean include(final Artifact artifact) {
                    assert artifact != null;
                    
                    boolean include = false;

                    if (filter != null) {
                        include = filter.include(artifact);
                    }

                    if (include) {
                        include = !pluginArtifactMap.containsKey(artifact.getGroupId() + ":" + artifact.getArtifactId());
                    }

                    return include;
                }
            };

            return artifactResolver.resolveTransitively(
                    Collections.singleton(artifact),
                    originating,
                    artifactRepository,
                    remoteRepositories,
                    artifactMetadataSource,
                    filters);
        }
    }
}