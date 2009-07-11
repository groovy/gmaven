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

package org.codehaus.groovy.maven.runtime.loader.realm;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.classworlds.ClassWorldException;
import org.codehaus.groovy.maven.feature.Provider;

import java.net.URL;

/**
 * Provides an abstraction to deal with ClassWorlds realms.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public interface RealmManager
{
    ClassRealm createProviderRealm(String key, URL[] classPath, ClassLoader parent) throws ClassWorldException;

    ClassRealm createComponentRealm(Provider provider, URL[] classPath) throws ClassWorldException;

    void releaseComponentRealm(ClassRealm realm) throws NoSuchRealmException;
}
