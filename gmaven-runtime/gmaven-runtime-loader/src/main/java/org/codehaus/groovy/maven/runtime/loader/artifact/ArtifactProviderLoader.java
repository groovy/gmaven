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

package org.codehaus.groovy.maven.runtime.loader.artifact;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.ProviderLoader;
import org.codehaus.groovy.maven.runtime.loader.LoaderSupport;
import org.codehaus.plexus.PlexusContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Loads a provider based on a configured {@link ArtifactHandler}.
 *
 * @plexus.component role="org.codehaus.groovy.maven.feature.ProviderLoader" role-hint="artifact"
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
*/
public class ArtifactProviderLoader
    extends LoaderSupport
    implements ProviderLoader
{
    private ArtifactHandler handler;

    public ArtifactProviderLoader() {}
    
    public ArtifactHandler getHandler() {
        return handler;
    }

    public void setHandler(final ArtifactHandler handler) {
        this.handler = handler;
    }

    public Map load(final String key) throws Exception {
        assert key != null;

        if (handler == null) {
            log.error("Artifact handler has not been configured; unable to load anything");
            return null;
        }

        log.debug("Loading providers: {}", key);

        Artifact query = handler.createQuery(key);
        
        return load(query);
    }

    private Map load(final Artifact query) throws Exception {
        assert query != null;

        PlexusContainer container = findContainer(query);

        // Get a hold on our child's class loader, we need to inject it into the thread context for shit to work
        ClassRealm childRealm = container.getContainerRealm();
        ClassLoader classLoader = childRealm.getClassLoader();

        Map discovered = null;

        // Need to have our child's CL as the TCL or shit won't work as we want :-(
        final ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        // See if there are any providers
        try {
            discovered = container.lookupMap(Provider.class.getName());
        }
        finally {
            Thread.currentThread().setContextClassLoader(tcl);
        }

        // If we didn't find anything, then nuke the child container
        if (discovered == null || discovered.isEmpty()) {
            container.dispose();
        }

        return discovered;
    }

    private PlexusContainer findContainer(final Artifact query) throws Exception {
        assert query != null;

        String id = query.getDependencyConflictId();

        PlexusContainer parent = getContainer();
        
        PlexusContainer container;

        if (parent.hasChildContainer(id)) {
            // Use the existing container
            container = parent.getChildContainer(id);

            log.trace("Re-using container: {}", container);
        }
        else {
            // Create a new child container, build its class-path
            List classPath = buildClassPath(query);

            Map context = new HashMap();

            // FIXME: This needs to change for Maven 2.1 support, which does not appear to have access to this method

            /*
            public PlexusContainer PlexusContainer.createChildContainer( String name,
                                                         List classpath,
                                                         Map context )
                throws PlexusContainerException
            {
                return createChildContainer( name, classpath, context, Collections.EMPTY_LIST );
            }

            public PlexusContainer PlexusContainer.createChildContainer( String name,
                                                         List classpath,
                                                         Map context,
                                                         List discoveryListeners )
                throws PlexusContainerException
            {
                if ( hasChildContainer( name ) )
                {
                    throw new DuplicateChildContainerException( getName(), name );
                }

                ClassRealm realm = new ClassRealm( new ClassWorld(), name );
                for ( Iterator it = classpath.iterator(); it.hasNext(); )
                {
                    File jar = (File) it.next();
                    try
                    {
                        realm.addURL( jar.toURI().toURL() );
                    }
                    catch ( MalformedURLException e )
                    {
                        throw new PlexusContainerException( "Cannot add jar resource: " + jar + " (bad URL)", e );
                    }
                }

                ContainerConfiguration config = new DefaultContainerConfiguration();
        
                config.setRealm( realm );
                config.setName( name );
                config.setParentContainer( this );
                config.setContext( context );

                for ( Iterator it = discoveryListeners.iterator(); it.hasNext(); )
                {
                    ComponentDiscoveryListener listener = (ComponentDiscoveryListener) it.next();
                    config.addComponentDiscoveryListener( listener );
                }

                return new DefaultPlexusContainer( config );
            }
             */
            container = parent.createChildContainer(id, classPath, context);

            log.trace("Created new container: {}", container);
        }

        return container;
    }

    private List buildClassPath(final Artifact query) throws Exception {
        assert query != null;

        Artifact artifact = handler.createDependency(query);
        ArtifactResolutionResult result = handler.resolve(artifact, new ScopeArtifactFilter(DefaultArtifact.SCOPE_RUNTIME));

        log.debug("Classpath: {}", artifact);

        List classPath = new ArrayList();

        // Add runtime dependency classpath
        for (Iterator iter = result.getArtifacts().iterator(); iter.hasNext();) {
            Artifact element = (Artifact) iter.next();

            File file = element.getFile();
            log.debug("    {}", file);

            classPath.add(file);
        }
        
        return classPath;
    }
}
