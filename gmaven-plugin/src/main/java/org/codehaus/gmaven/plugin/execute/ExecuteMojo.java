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

package org.codehaus.gmaven.plugin.execute;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.codehaus.gmaven.common.ArtifactItem;
import org.codehaus.gmaven.feature.Component;
import org.codehaus.gmaven.feature.Configuration;
import org.codehaus.gmaven.plugin.ComponentMojoSupport;
import org.codehaus.gmaven.runtime.ScriptExecutor;
import org.codehaus.gmaven.runtime.loader.realm.RealmManager;
import org.codehaus.gmaven.runtime.support.util.ResourceLoaderImpl;
import org.codehaus.gmaven.runtime.util.Callable;
import org.codehaus.gmaven.runtime.util.ClassSource;
import org.codehaus.gmaven.runtime.util.MagicAttribute;
import org.codehaus.gmaven.runtime.util.ResourceLoader;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Executes a Groovy script.
 *
 * @goal execute
 * @requiresDependencyResolution test
 * @configurator override
 * @since 1.0-alpha-1
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class ExecuteMojo
    extends ComponentMojoSupport
{
    /** 
     * The plugin dependencies.
     *
     * @parameter expression="${plugin.artifacts}" 
     * @noinspection UnusedDeclaration
     * @readonly
     */
    private List pluginArtifacts;

    /**
     * The source of the script to execute.  This can be a URL, File or script body.
     *
     * @parameter
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    private Source source;

    /**
     * Additional artifacts to add to the scripts classpath.
     *
     * @parameter
     *
     * @noinspection UnusedDeclaration,MismatchedReadAndWriteOfArray
     */
    private ArtifactItem[] classpath;

    public static final String CLASSPATH_INCLUDE_ALL = "all";

    public static final String CLASSPATH_INCLUDE_RUNTIME = "runtime";

    public static final String CLASSPATH_INCLUDE_ARTIFACTS = "artifacts";

    public static final String CLASSPATH_INCLUDE_PLUGINS = "plugins";

    public static final String CLASSPATH_INCLUDE_NONE = "none";

    /**
     * Allows control over what classpath elements are included.
     * Comma separated list which can contain one or more of: all, runtime, artifacts, plugins, none.
     *
     * @parameter default-value="all"
     * @required
     *
     * @noinspection UnusedDeclaration
     *
     * @since 1.5
     */
    private String classpathIncludes = CLASSPATH_INCLUDE_ALL;

    /**
     * Path to search for imported scripts.
     *
     * @parameter
     *
     * @noinspection UnusedDeclaration,MismatchedReadAndWriteOfArray
     */
    private File[] scriptpath;

    /**
     * A set of default project properties, which the values will be used only if
     * the project or system does not override.
     *
     * @parameter
     *
     * @noinspection UnusedDeclaration
     */
    private Map defaults;

    /**
     * A set of additional project properties.
     * 
     * @parameter
     *
     * @noinspection UnusedDeclaration
     */
    private Map properties;

    /**
     * Trap assertion errors and rethrow them as execution failures.
     *
     * @parameter default-value="true"
     *
     * @noinspection UnusedDeclaration
     */
    private boolean trapAssertionErrors;

    /**
     * Sanatize errors, stripping out Groovy internals.
     *
     * @parameter default-value="true"
     * @since 1.0-beta-3
     *
     * @noinspection UnusedDeclaration
     */
    private boolean sanitizeErrors;

    /**
     * @parameter expression="${session}"
     * @readonly
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    private MavenSession session;

    /**
     * @parameter expression="${settings}"
     * @readonly
     * @required
     *
     * @noinspection UnusedDeclaration
     */
    private Settings settings;

    /**
     * @component
     *
     * @noinspection UnusedDeclaration
     */
    private RealmManager realmManager;

    public ExecuteMojo() {
        super(ScriptExecutor.KEY);
    }

    /**
     * @since 1.5
     */
    protected Set getClasspathIncludes() {
        Set includes = new HashSet();
        String[] items = classpathIncludes.trim().split(",");
        for (int i=0; i<items.length; i++) {
            items[i] = items[i].trim().toLowerCase();

            // If we find none, then ignore everything else
            if (CLASSPATH_INCLUDE_NONE.equals(items[i])) {
                return new HashSet();
            }

            includes.add(items[i]);
        }

        return includes;
    }

    /**
     * Allow the script to work with every JAR dependency of both the project and plugin, including
     * optional and provided dependencies. Runtime classpath elements are loaded first, so that 
     * legacy behavior is not modified.  Additional elements are added first in the order of 
     * project artifacts, then in the order of plugin artifacts.
     */
    protected List getProjectClasspathElements() throws DependencyResolutionRequiredException {
        Set results = new LinkedHashSet();

        Set includes = getClasspathIncludes();

        if (includes.contains(CLASSPATH_INCLUDE_ALL) || includes.contains(CLASSPATH_INCLUDE_RUNTIME)) {
            for (Iterator i = project.getRuntimeClasspathElements().iterator(); i.hasNext();) {
                String fileName = (String) i.next();
                try {
                    results.add(new File(fileName).getCanonicalPath());
                }
                catch (IOException e) {
                    throw new RuntimeException("Classpath element not found: " + fileName, e);
                }
            }
        }

        if (includes.contains(CLASSPATH_INCLUDE_ALL) || includes.contains(CLASSPATH_INCLUDE_ARTIFACTS)) {
            for (Iterator i = project.getArtifacts().iterator(); i.hasNext();) {
                Artifact artifact = (Artifact) i.next();
                if (artifact.getType().equals("jar") && artifact.getClassifier() == null) {
                    try {
                        results.add(artifact.getFile().getCanonicalPath());
                    }
                    catch (IOException e) {
                        throw new RuntimeException("Maven artifact file not found: " + artifact, e);
                    }
                }
            }
        }

        if (includes.contains(CLASSPATH_INCLUDE_ALL) || includes.contains(CLASSPATH_INCLUDE_PLUGINS)) {
            for (Iterator i = pluginArtifacts.iterator(); i.hasNext();) {
                Artifact artifact = (Artifact) i.next();
                if (artifact.getType().equals("jar") && artifact.getClassifier() == null) {
                    try {
                        results.add(artifact.getFile().getCanonicalPath());
                    }
                    catch (IOException e) {
                        throw new RuntimeException("Maven plugin-artifact file not found: " + artifact, e);
                    }
                }
            }
        }

        return new ArrayList(results);
    }

    protected ArtifactItem[] getUserClassspathElements() {
        return classpath;
    }

    protected void process(final Component component) throws Exception {
        assert component != null;

        ScriptExecutor executor = (ScriptExecutor)component;

        if (source.configuration.getChildCount() != 0) {
            throw new MojoExecutionException("Invalid value for 'source' parameter; contains nested elements");
        }

        ClassSource classSource = ClassSource.forValue(escapeAsNeeded(source.configuration.getValue()));
        log.debug("Class source: {}", classSource);

        ClassRealm realm = realmManager.createComponentRealm(provider(), createClassPath());

        ResourceLoader resourceLoader = new MojoResourceLoader(realm, classSource);

        Configuration context = createContext();

        log.debug("Executing '''{}''' w/context: {}", source, context);

        Object result = executor.execute(classSource, realm, resourceLoader, context);

        log.debug("Result: {}", result);

        realmManager.releaseComponentRealm(realm);
    }

    protected String escapeAsNeeded(String string) {
        StringBuilder sb = new StringBuilder(string);
        int index = 0;
        while (index > -1) {
            int i = sb.indexOf("\\", index);
            if (i > -1) {
                String str = sb.substring(i, i + 2);
                if (!str.contains("\\\\")) {
                    sb.insert(i, "\\");
                } else {
                    sb.delete(i, i + 1);
                }
                index = i + 3;
            } else {
                index = i;
            }
        }

        return sb.toString();
    }

    private Configuration createContext() {
        Configuration context = new Configuration();

        // Expose logging, give it a new logger that has better chances of being configured if needed
        // would be nice to get the execution id in here...
        Logger logger = LoggerFactory.getLogger(project.getGroupId() + "." + project.getArtifactId() + ".ExecuteMojo");
        context.set("log", logger);

        // Add a custom project to resolve properties
        MavenProject projectAdapter = new GroovyMavenProjectAdapter(project, session, properties, defaults);
        context.set("project", projectAdapter);
        context.set("pom", projectAdapter);

        // Stuff in some other Maven bits
        context.set("session", session);
        context.set("settings", settings);

        // Stuff in an Ant helper
        context.set("ant", MagicAttribute.ANT_BUILDER);

        // Stuff on a fail helper
        context.set("fail", new FailClosure());

        return context;
    }

    //
    // MojoResourceLoader
    //

    private class MojoResourceLoader
        extends ResourceLoaderImpl
    {
        private final ClassSource classSource;

        public MojoResourceLoader(final URLClassLoader classLoader, final ClassSource classSource) {
            super(classLoader);

            assert classSource != null;

            this.classSource = classSource;
        }

        protected URL resolve(final String className, final ClassLoader classLoader) throws MalformedURLException {
            assert className != null;
            assert classLoader != null;

            String resource = toResourceName(className);

            URL url;

            // First check the scriptpath
            if (scriptpath != null) {
                for (int i=0; i<scriptpath.length; i++) {
                    assert scriptpath[i] != null;

                    File file = new File(scriptpath[i], resource);

                    if (file.exists()) {
                        return file.toURI().toURL();
                    }
                }
            }

            // Then look for a resource in the classpath
            url = classLoader.getResource(resource);

            // Try w/o leading '/'... ???  Seems that when loading resources the '/' prefix messes things up?
            if (url == null) {
                if (resource.startsWith("/")) {
                    String tmp = resource.substring(1, resource.length());

                    url = classLoader.getResource(tmp);
                }
            }

            if (url == null) {
                // And finally check for a class defined in a file next to the main script file
                File script = classSource.file;

                if (script != null) {
                    File file = new File(script.getParentFile(), resource);

                    if (file.exists()) {
                        return file.toURI().toURL();
                    }
                }
            }
            else {
                return url;
            }

            return super.resolve(className, classLoader);
        }
    }

    //
    // FailClosure
    //

    private class FailClosure
        implements Callable
    {
        public Object call(final Object[] args) throws Exception {
            if (args == null || args.length == 0) {
                throw new MojoExecutionException("Failed");
            }
            else if (args.length == 1) {
                if (args[0] instanceof Throwable) {
                    Throwable cause = (Throwable)args[0];
                    throw new MojoExecutionException(cause.getMessage(), cause);
                }
                else {
                    throw new MojoExecutionException(String.valueOf(args[0]));
                }
            }
            else if (args.length == 2) {
                if (args[1] instanceof Throwable) {
                    throw new MojoExecutionException(String.valueOf(args[0]), (Throwable)args[1]);
                }
                else {
                    throw new Error("Invalid arguments to fail(Object, Throwable), second argument must be a Throwable");
                }
            }
            else {
                throw new Error("Too many arguments for fail(), expected one of: fail(), fail(Object) or fail(Object, Throwable)");
            }
        }
    }
}