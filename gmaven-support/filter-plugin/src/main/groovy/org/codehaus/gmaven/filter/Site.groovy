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

package org.codehaus.gmaven.filter;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.filtering.MavenResourcesExecution;
import org.apache.maven.shared.filtering.MavenResourcesFiltering;

/**
 * This simply performs standard Maven filtering on <tt>/target/site</tt>.  The result is in
 * the same folder, so <tt>site:deploy</tt> works without a hitch.  This is much simpler to use than
 * the filtering mechanism built into the site plugin.
 *
 * @author Jason Smith
 * @requiresDependencyResolution compile
 * @goal site
 * @phase process-resources
 */
public class Site
    extends AbstractMojo
{
    /**
     * The project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The current session.
     * @parameter expression="${session}"
     * @readonly
     * @required
     */
    protected MavenSession session;

    /**
     * The filtering object.
     * @component role="org.apache.maven.shared.filtering.MavenResourcesFiltering" role-hint="default"
     * @required
     */
    protected MavenResourcesFiltering mavenResourcesFiltering;

    /**
     * The character encoding scheme to be applied when filtering resources.
     * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
     */
    protected String encoding;

    /**
     * Expression preceded with the String won't be interpolated 
     * \${foo} will be replaced with ${foo}* @parameter expression="${maven.resources.escapeString}"
     */
    protected String escapeString;

    /**
     * Provides filtering for the site.
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try {
            Resource resource = new Resource();
            resource.setFiltering(true);
            resource.setDirectory(new File("${project.getBuild().getDirectory()}/site").getCanonicalPath());

            MavenResourcesExecution mavenResourcesExecution = new MavenResourcesExecution(
                    Arrays.asList([resource] as Resource[]),
                    new File("${project.getBuild().getDirectory()}/filtered-site"),
                    project,
                    encoding,
                    Collections.EMPTY_LIST,
                    Collections.EMPTY_LIST,
                    session);

            mavenResourcesExecution.setEscapeString(escapeString);
            mavenResourcesExecution.setOverwrite(false);
            mavenResourcesExecution.setIncludeEmptyDirs(true);

            mavenResourcesFiltering.filterResources(mavenResourcesExecution);

            AntBuilder ant = new AntBuilder();
            ant.delete(dir: new File("${project.getBuild().getDirectory()}/site").getCanonicalPath());
            ant.copy(todir: new File("${project.getBuild().getDirectory()}/site").getCanonicalPath(), overwrite: true)
            {
                fileset(dir: new File("${project.getBuild().getDirectory()}/filtered-site").getCanonicalPath())
            }
        }
        catch (Exception e) {
            throw new MojoExecutionException('I\'ve lost my mojo!', e);
        }
    }
}
