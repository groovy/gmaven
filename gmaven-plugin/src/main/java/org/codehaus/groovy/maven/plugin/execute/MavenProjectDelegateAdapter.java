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

package org.codehaus.groovy.maven.plugin.execute;

import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Model;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Scm;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.License;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Plugin;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.IOException;
import java.io.File;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Properties;

/**
 * {@link MavenProject} delegation adapter.
 *
 * @version $Id$
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class MavenProjectDelegateAdapter
    extends MavenProject
{
    private final MavenProject delegate;

    public MavenProjectDelegateAdapter(final MavenProject project) {
        assert project != null;

        this.delegate = project;
    }

    public MavenProject getDelegate() {
        return delegate;
    }

    public String getModulePathAdjustment(final MavenProject project) throws IOException {
        return getDelegate().getModulePathAdjustment(project);
    }

    public Artifact getArtifact() {
        return getDelegate().getArtifact();
    }

    public void setArtifact(final Artifact artifact) {
        getDelegate().setArtifact(artifact);
    }

    public Model getModel() {
        return getDelegate().getModel();
    }

    public MavenProject getParent() {
        return getDelegate().getParent();
    }

    public void setParent(final MavenProject project) {
        getDelegate().setParent(project);
    }

    public void setRemoteArtifactRepositories(final List list) {
        getDelegate().setRemoteArtifactRepositories(list);
    }

    public List getRemoteArtifactRepositories() {
        return getDelegate().getRemoteArtifactRepositories();
    }

    public boolean hasParent() {
        return getDelegate().hasParent();
    }

    public File getFile() {
        return getDelegate().getFile();
    }

    public void setFile(File file) {
        getDelegate().setFile(file);
    }

    public File getBasedir() {
        return getDelegate().getBasedir();
    }

    public void setDependencies(List list) {
        getDelegate().setDependencies(list);
    }

    public List getDependencies() {
        return getDelegate().getDependencies();
    }

    public DependencyManagement getDependencyManagement() {
        return getDelegate().getDependencyManagement();
    }

    public void addCompileSourceRoot(final String root) {
        getDelegate().addCompileSourceRoot(root);
    }

    public void addScriptSourceRoot(final String root) {
        getDelegate().addScriptSourceRoot(root);
    }

    public void addTestCompileSourceRoot(final String root) {
        getDelegate().addTestCompileSourceRoot(root);
    }

    public List getCompileSourceRoots() {
        return getDelegate().getCompileSourceRoots();
    }

    public List getScriptSourceRoots() {
        return getDelegate().getScriptSourceRoots();
    }

    public List getTestCompileSourceRoots() {
        return getDelegate().getTestCompileSourceRoots();
    }

    public List getCompileClasspathElements() throws DependencyResolutionRequiredException {
        return getDelegate().getCompileClasspathElements();
    }

    public List getCompileArtifacts() {
        return getDelegate().getCompileArtifacts();
    }

    public List getCompileDependencies() {
        return getDelegate().getCompileDependencies();
    }

    public List getTestClasspathElements() throws DependencyResolutionRequiredException {
        return getDelegate().getTestClasspathElements();
    }

    public List getTestArtifacts() {
        return getDelegate().getTestArtifacts();
    }

    public List getTestDependencies() {
        return getDelegate().getTestDependencies();
    }

    public List getRuntimeClasspathElements() throws DependencyResolutionRequiredException {
        return getDelegate().getRuntimeClasspathElements();
    }

    public List getRuntimeArtifacts() {
        return getDelegate().getRuntimeArtifacts();
    }

    public List getRuntimeDependencies() {
        return getDelegate().getRuntimeDependencies();
    }

    public List getSystemClasspathElements() throws DependencyResolutionRequiredException {
        return getDelegate().getSystemClasspathElements();
    }

    public List getSystemArtifacts() {
        return getDelegate().getSystemArtifacts();
    }

    public List getSystemDependencies() {
        return getDelegate().getSystemDependencies();
    }

    public void setModelVersion(final String version) {
        getDelegate().setModelVersion(version);
    }

    public String getModelVersion() {
        return getDelegate().getModelVersion();
    }

    public String getId() {
        return getDelegate().getId();
    }

    public void setGroupId(final String id) {
        getDelegate().setGroupId(id);
    }

    public String getGroupId() {
        return getDelegate().getGroupId();
    }

    public void setArtifactId(final String id) {
        getDelegate().setArtifactId(id);
    }

    public String getArtifactId() {
        return getDelegate().getArtifactId();
    }

    public void setName(final String name) {
        getDelegate().setName(name);
    }

    public String getName() {
        return getDelegate().getName();
    }

    public void setVersion(final String version) {
        getDelegate().setVersion(version);
    }

    public String getVersion() {
        return getDelegate().getVersion();
    }

    public String getPackaging() {
        return getDelegate().getPackaging();
    }

    public void setPackaging(final String s) {
        getDelegate().setPackaging(s);
    }

    public void setInceptionYear(final String s) {
        getDelegate().setInceptionYear(s);
    }

    public String getInceptionYear() {
        return getDelegate().getInceptionYear();
    }

    public void setUrl(final String url) {
        getDelegate().setUrl(url);
    }

    public String getUrl() {
        return getDelegate().getUrl();
    }

    public Prerequisites getPrerequisites() {
        return getDelegate().getPrerequisites();
    }

    public void setIssueManagement(final IssueManagement management) {
        getDelegate().setIssueManagement(management);
    }

    public CiManagement getCiManagement() {
        return getDelegate().getCiManagement();
    }

    public void setCiManagement(final CiManagement management) {
        getDelegate().setCiManagement(management);
    }

    public IssueManagement getIssueManagement() {
        return getDelegate().getIssueManagement();
    }

    public void setDistributionManagement(final DistributionManagement management) {
        getDelegate().setDistributionManagement(management);
    }

    public DistributionManagement getDistributionManagement() {
        return getDelegate().getDistributionManagement();
    }

    public void setDescription(final String s) {
        getDelegate().setDescription(s);
    }

    public String getDescription() {
        return getDelegate().getDescription();
    }

    public void setOrganization(final Organization organization) {
        getDelegate().setOrganization(organization);
    }

    public Organization getOrganization() {
        return getDelegate().getOrganization();
    }

    public void setScm(final Scm scm) {
        getDelegate().setScm(scm);
    }

    public Scm getScm() {
        return getDelegate().getScm();
    }

    public void setMailingLists(final List list) {
        getDelegate().setMailingLists(list);
    }

    public List getMailingLists() {
        return getDelegate().getMailingLists();
    }

    public void addMailingList(final MailingList mailingList) {
        getDelegate().addMailingList(mailingList);
    }

    public void setDevelopers(final List list) {
        getDelegate().setDevelopers(list);
    }

    public List getDevelopers() {
        return getDelegate().getDevelopers();
    }

    public void addDeveloper(final Developer developer) {
        getDelegate().addDeveloper(developer);
    }

    public void setContributors(final List list) {
        getDelegate().setContributors(list);
    }

    public List getContributors() {
        return getDelegate().getContributors();
    }

    public void addContributor(final Contributor contributor) {
        getDelegate().addContributor(contributor);
    }

    public void setBuild(final Build build) {
        getDelegate().setBuild(build);
    }

    public Build getBuild() {
        return getDelegate().getBuild();
    }

    public List getResources() {
        return getDelegate().getResources();
    }

    public List getTestResources() {
        return getDelegate().getTestResources();
    }

    public void addResource(final Resource resource) {
        getDelegate().addResource(resource);
    }

    public void addTestResource(final Resource resource) {
        getDelegate().addTestResource(resource);
    }

    public void setReporting(final Reporting reporting) {
        getDelegate().setReporting(reporting);
    }

    public Reporting getReporting() {
        return getDelegate().getReporting();
    }

    public void setLicenses(final List list) {
        getDelegate().setLicenses(list);
    }

    public List getLicenses() {
        return getDelegate().getLicenses();
    }

    public void addLicense(final License license) {
        getDelegate().addLicense(license);
    }

    public void setArtifacts(final Set set) {
        getDelegate().setArtifacts(set);
    }

    public Set getArtifacts() {
        return getDelegate().getArtifacts();
    }

    public Map getArtifactMap() {
        return getDelegate().getArtifactMap();
    }

    public void setPluginArtifacts(final Set set) {
        getDelegate().setPluginArtifacts(set);
    }

    public Set getPluginArtifacts() {
        return getDelegate().getPluginArtifacts();
    }

    public Map getPluginArtifactMap() {
        return getDelegate().getPluginArtifactMap();
    }

    public void setReportArtifacts(final Set set) {
        getDelegate().setReportArtifacts(set);
    }

    public Set getReportArtifacts() {
        return getDelegate().getReportArtifacts();
    }

    public Map getReportArtifactMap() {
        return getDelegate().getReportArtifactMap();
    }

    public void setExtensionArtifacts(final Set set) {
        getDelegate().setExtensionArtifacts(set);
    }

    public Set getExtensionArtifacts() {
        return getDelegate().getExtensionArtifacts();
    }

    public Map getExtensionArtifactMap() {
        return getDelegate().getExtensionArtifactMap();
    }

    public void setParentArtifact(final Artifact artifact) {
        getDelegate().setParentArtifact(artifact);
    }

    public Artifact getParentArtifact() {
        return getDelegate().getParentArtifact();
    }

    public List getRepositories() {
        return getDelegate().getRepositories();
    }

    public List getReportPlugins() {
        return getDelegate().getReportPlugins();
    }

    public List getBuildPlugins() {
        return getDelegate().getBuildPlugins();
    }

    public List getModules() {
        return getDelegate().getModules();
    }

    public PluginManagement getPluginManagement() {
        return getDelegate().getPluginManagement();
    }

    public void addPlugin(final Plugin plugin) {
        getDelegate().addPlugin(plugin);
    }

    public void injectPluginManagementInfo(final Plugin plugin) {
        getDelegate().injectPluginManagementInfo(plugin);
    }

    public List getCollectedProjects() {
        return getDelegate().getCollectedProjects();
    }

    public void setCollectedProjects(final List list) {
        getDelegate().setCollectedProjects(list);
    }

    public void setPluginArtifactRepositories(final List list) {
        getDelegate().setPluginArtifactRepositories(list);
    }

    public List getPluginArtifactRepositories() {
        return getDelegate().getPluginArtifactRepositories();
    }

    public ArtifactRepository getDistributionManagementArtifactRepository() {
        return getDelegate().getDistributionManagementArtifactRepository();
    }

    public List getPluginRepositories() {
        return getDelegate().getPluginRepositories();
    }

    public void setActiveProfiles(final List list) {
        getDelegate().setActiveProfiles(list);
    }

    public List getActiveProfiles() {
        return getDelegate().getActiveProfiles();
    }

    public void addAttachedArtifact(final Artifact artifact) {
        getDelegate().addAttachedArtifact(artifact);
    }

    public List getAttachedArtifacts() {
        return getDelegate().getAttachedArtifacts();
    }

    public Xpp3Dom getGoalConfiguration(final String s, final String s1, final String s2, final String s3) {
        return getDelegate().getGoalConfiguration(s, s1, s2, s3);
    }

    public Xpp3Dom getReportConfiguration(final String s, final String s1, final String s2) {
        return getDelegate().getReportConfiguration(s, s1, s2);
    }

    public MavenProject getExecutionProject() {
        return getDelegate().getExecutionProject();
    }

    public void setExecutionProject(final MavenProject project) {
        getDelegate().setExecutionProject(project);
    }

    public void writeModel(final Writer writer) throws IOException {
        getDelegate().writeModel(writer);
    }

    public void writeOriginalModel(final Writer writer) throws IOException {
        getDelegate().writeOriginalModel(writer);
    }

    public Set getDependencyArtifacts() {
        return getDelegate().getDependencyArtifacts();
    }

    public void setDependencyArtifacts(final Set set) {
        getDelegate().setDependencyArtifacts(set);
    }

    public void setReleaseArtifactRepository(final ArtifactRepository repository) {
        getDelegate().setReleaseArtifactRepository(repository);
    }

    public void setSnapshotArtifactRepository(final ArtifactRepository repository) {
        getDelegate().setSnapshotArtifactRepository(repository);
    }

    public void setOriginalModel(final Model model) {
        getDelegate().setOriginalModel(model);
    }

    public Model getOriginalModel() {
        return getDelegate().getOriginalModel();
    }

    public List getBuildExtensions() {
        return getDelegate().getBuildExtensions();
    }

    public Set createArtifacts(final ArtifactFactory factory, final String s, final ArtifactFilter filter) throws InvalidDependencyVersionException {
        return getDelegate().createArtifacts(factory, s, filter);
    }

    public void addProjectReference(final MavenProject project) {
        getDelegate().addProjectReference(project);
    }

    /** @noinspection deprecation*/
    public void attachArtifact(final String s, final String s1, final File file) {
        getDelegate().attachArtifact(s, s1, file);
    }

    public Properties getProperties() {
        return getDelegate().getProperties();
    }

    public List getFilters() {
        return getDelegate().getFilters();
    }

    public Map getProjectReferences() {
        return getDelegate().getProjectReferences();
    }

    public boolean isExecutionRoot() {
        return getDelegate().isExecutionRoot();
    }

    public void setExecutionRoot(final boolean b) {
        getDelegate().setExecutionRoot(b);
    }

    public String getDefaultGoal() {
        return getDelegate().getDefaultGoal();
    }

    public Artifact replaceWithActiveArtifact(final Artifact artifact) {
        return getDelegate().replaceWithActiveArtifact(artifact);
    }
}