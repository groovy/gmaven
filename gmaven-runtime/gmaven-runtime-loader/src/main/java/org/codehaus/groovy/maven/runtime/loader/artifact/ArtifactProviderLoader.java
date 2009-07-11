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
import org.codehaus.groovy.maven.feature.Provider;
import org.codehaus.groovy.maven.feature.ProviderLoader;
import org.codehaus.groovy.maven.runtime.loader.realm.RealmManager;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
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
    implements ProviderLoader
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * @plexus.requirement
     * 
     * @noinspection UnusedDeclaration
     */
    private RealmManager realmManager;

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

        Provider provider = loadProvider(key);

        Map providers = new HashMap();
        
        providers.put(provider.key(), provider);

        return providers;
    }

    private URL[] buildClassPath(final Artifact query) throws Exception {
        assert query != null;

        Artifact artifact = handler.createDependency(query);
        ArtifactResolutionResult result = handler.resolve(artifact, new ScopeArtifactFilter(DefaultArtifact.SCOPE_RUNTIME));

        List classPath = new ArrayList();

        // Add runtime dependency classpath
        for (Iterator iter = result.getArtifacts().iterator(); iter.hasNext();) {
            Artifact element = (Artifact) iter.next();

            File file = element.getFile();
            URL url = file.toURI().toURL();

            classPath.add(url);
        }

        return (URL[]) classPath.toArray(new URL[classPath.size()]);
    }

    private Provider loadProvider(final String key) throws Exception {
        assert key != null;

        log.debug("Loading providers: {}", key);

        Artifact query = handler.createQuery(key);
        URL[] classPath = buildClassPath(query);
        ClassLoader parent = getClass().getClassLoader();
        ClassRealm realm = realmManager.createProviderRealm(key, classPath, parent);
        
        Class type = realm.loadClass("org.codehaus.groovy.maven.runtime.v" + key.replace('.', '_').replace('-', '_') + ".ProviderImpl");

        return (Provider) type.newInstance();
    }
}
