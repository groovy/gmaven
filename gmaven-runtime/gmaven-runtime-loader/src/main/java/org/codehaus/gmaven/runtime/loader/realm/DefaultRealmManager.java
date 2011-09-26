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

package org.codehaus.gmaven.runtime.loader.realm;

import org.codehaus.gmaven.feature.Provider;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.ClassWorldException;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.classworlds.strategy.ParentFirstStrategy;
import org.codehaus.plexus.classworlds.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The default {@link RealmManager} component.
 *
 * @plexus.component role="org.codehaus.gmaven.runtime.loader.realm.RealmManager"
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class DefaultRealmManager
    implements RealmManager
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private ClassWorld classWorld = new ClassWorld();

    private Map providerRealms = new HashMap();

    public ClassRealm createProviderRealm(final String key, final URL[] classPath, final ClassLoader parent) throws ClassWorldException {
        assert key != null;
        assert classPath != null;
        assert parent != null;

        String id = Provider.class.getName() + "[" + key + "]";

        log.debug("Creating provider realm: {}", id);

        ClassRealm realm = classWorld.newRealm(id, parent);
        setupRealm(realm, classPath);

        providerRealms.put(key, realm);

        return realm;
    }

    private int uniqueCounter = 0;

    private synchronized String uniqueId() {
        return System.currentTimeMillis() + ":" + (uniqueCounter++);
    }

    public ClassRealm createComponentRealm(final Provider provider, final URL[] classPath) throws ClassWorldException {
        assert provider != null;
        assert classPath != null;

        String id = provider.getClass().getName() + "#component[" + uniqueId() + "]";

        log.debug("Creating component realm: {}", id);

        ClassRealm providerRealm = (ClassRealm)providerRealms.get(provider.key());

        if (providerRealm == null) {
            throw new Error("No realm for provider: " + provider);
        }

        log.debug("    Provider realm: {}", providerRealm.getId());

        ClassRealm realm = providerRealm.createChildRealm(id);
        setupRealm(realm, classPath);

        return realm;
    }

    public void releaseComponentRealm(final ClassRealm realm) throws NoSuchRealmException {
        assert realm != null;

        log.debug("Releasing component realm: {}", realm.getId());

        classWorld.disposeRealm(realm.getId());
    }

    private void setupRealm(final ClassRealm realm, final URL[] classPath) {
        assert realm != null;
        assert classPath != null;

        // HACK: Force the realm to use parent-first, instead of the default self-first
        setStrategy(realm, new ParentFirstStrategy(realm));

        for (int i=0; i<classPath.length; i++) {
            realm.addURL(classPath[i]);

            log.debug("    {}", classPath[i]);
        }

        if (log.isTraceEnabled()) {
            ByteArrayOutputStream buff = new ByteArrayOutputStream();
            realm.display(new PrintStream(buff, true));
            log.trace("Realm configuration:\n" + new String(buff.toByteArray()));
        }
    }

    /**
     * There is no public API to set/change a realms strategy, so we use some reflection muck to set the private field.
     */
    private void setStrategy(final ClassRealm realm, final Strategy strategy) {
        assert realm != null;
        assert strategy != null;

        try {
            Field field = realm.getClass().getDeclaredField("strategy");

            try {
                field.set(realm, strategy);
            }
            catch (IllegalAccessException ignore) {
                // try again
                field.setAccessible(true);

                try {
                    field.set(realm, strategy);
                }
                catch (IllegalAccessException e) {
                    throw new IllegalAccessError(e.getMessage());
                }
            }
        }
        catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }
}