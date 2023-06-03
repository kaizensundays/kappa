package org.apache.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.*;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Maven 3.5.4 AbstractCoreMavenComponentTestCase modified for JUnit 5
 */
@SuppressWarnings("unused")
public abstract class AbstractCoreMavenComponentTestSupport extends PlexusTestSupport {

    @Requirement
    protected RepositorySystem repositorySystem;

    @Requirement
    protected org.apache.maven.project.ProjectBuilder projectBuilder;

    @BeforeEach
    public void before() throws Exception {
        repositorySystem = lookup(RepositorySystem.class);
        projectBuilder = lookup(org.apache.maven.project.ProjectBuilder.class);
    }

    @AfterEach
    public void after() {
        repositorySystem = null;
        projectBuilder = null;
        super.tearDown();
    }

    abstract protected String getProjectsDirectory();

    protected File getProject(String name)
            throws Exception {
        File source = new File(new File(getBasedir(), getProjectsDirectory()), name);
        File target = new File(new File(getBasedir(), "target"), name);
        FileUtils.copyDirectoryStructureIfModified(source, target);
        return new File(target, "pom.xml");
    }

    /**
     * We need to customize the standard Plexus container with the plugin discovery listener which
     * is what looks for the META-INF/maven/plugin.xml resources that enter the system when a Maven
     * plugin is loaded.
     * <p>
     * We also need to customize the Plexus container with a standard plugin discovery listener
     * which is the MavenPluginCollector. When a Maven plugin is discovered the MavenPluginCollector
     * collects the plugin descriptors which are found.
     */
    protected void customizeContainerConfiguration(ContainerConfiguration containerConfiguration) {
        containerConfiguration.setAutoWiring(true).setClassPathScanning(PlexusConstants.SCANNING_INDEX);
    }

    protected MavenExecutionRequest createMavenExecutionRequest(File pom)
            throws Exception {
        //noinspection UnnecessaryLocalVariable
        MavenExecutionRequest request = new DefaultMavenExecutionRequest()
                .setPom(pom)
                .setProjectPresent(true)
                .setShowErrors(true)
                .setPluginGroups(Collections.singletonList("org.apache.maven.plugins"))
                .setLocalRepository(getLocalRepository())
                .setRemoteRepositories(getRemoteRepositories())
                .setPluginArtifactRepositories(getPluginArtifactRepositories())
                .setGoals(Collections.singletonList("package"));

        return request;
    }

    // layer the creation of a project builder configuration with a request, but this will need to be
    // a Maven subclass because we don't want to couple maven to the project builder which we need to
    // separate.
    protected MavenSession createMavenSession(File pom)
            throws Exception {
        return createMavenSession(pom, new Properties());
    }

    protected MavenSession createMavenSession(File pom, Properties executionProperties)
            throws Exception {
        return createMavenSession(pom, executionProperties, false);
    }

    protected MavenSession createMavenSession(File pom, Properties executionProperties, boolean includeModules)
            throws Exception {
        MavenExecutionRequest request = createMavenExecutionRequest(pom);

        ProjectBuildingRequest configuration = new DefaultProjectBuildingRequest()
                .setLocalRepository(request.getLocalRepository())
                .setRemoteRepositories(request.getRemoteRepositories())
                .setPluginArtifactRepositories(request.getPluginArtifactRepositories())
                .setSystemProperties(executionProperties);

        List<MavenProject> projects = new ArrayList<>();

        if (pom != null) {
            MavenProject project = projectBuilder.build(pom, configuration).getProject();

            projects.add(project);
            if (includeModules) {
                for (String module : project.getModules()) {
                    File modulePom = new File(pom.getParentFile(), module);
                    if (modulePom.isDirectory()) {
                        modulePom = new File(modulePom, "pom.xml");
                    }
                    projects.add(projectBuilder.build(modulePom, configuration).getProject());
                }
            }
        } else {
            MavenProject project = createStubMavenProject();
            project.setRemoteArtifactRepositories(request.getRemoteRepositories());
            project.setPluginArtifactRepositories(request.getPluginArtifactRepositories());
            projects.add(project);
        }

        initRepoSession(configuration);

        //noinspection deprecation
        MavenSession session =
                new MavenSession(getContainer(), configuration.getRepositorySession(), request,
                        new DefaultMavenExecutionResult());
        session.setProjects(projects);
        session.setAllProjects(session.getProjects());

        return session;
    }

    protected void initRepoSession(ProjectBuildingRequest request)
            throws Exception {
        File localRepoDir = new File(request.getLocalRepository().getBasedir());
        LocalRepository localRepo = new LocalRepository(localRepoDir);
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        session.setLocalRepositoryManager(new SimpleLocalRepositoryManagerFactory().newInstance(session, localRepo));
        request.setRepositorySession(session);
    }

    protected MavenProject createStubMavenProject() {
        Model model = new Model();
        model.setGroupId("org.apache.maven.test");
        model.setArtifactId("maven-test");
        model.setVersion("1.0");
        return new MavenProject(model);
    }

    protected List<ArtifactRepository> getRemoteRepositories()
            throws InvalidRepositoryException {
        File repoDir = new File(getBasedir(), "src/test/remote-repo").getAbsoluteFile();

        RepositoryPolicy policy = new RepositoryPolicy();
        policy.setEnabled(true);
        policy.setChecksumPolicy("ignore");
        policy.setUpdatePolicy("always");

        Repository repository = new Repository();
        repository.setId(RepositorySystem.DEFAULT_REMOTE_REPO_ID);
        repository.setUrl("file://" + repoDir.toURI().getPath());
        repository.setReleases(policy);
        repository.setSnapshots(policy);

        return Collections.singletonList(repositorySystem.buildArtifactRepository(repository));
    }

    protected List<ArtifactRepository> getPluginArtifactRepositories()
            throws InvalidRepositoryException {
        return getRemoteRepositories();
    }

    protected ArtifactRepository getLocalRepository()
            throws InvalidRepositoryException {
        File repoDir = new File(getBasedir(), "target/local-repo").getAbsoluteFile();

        return repositorySystem.createLocalRepository(repoDir);
    }

    protected static class ProjectBuilder {
        private final MavenProject project;

        public ProjectBuilder(MavenProject project) {
            this.project = project;
        }

        public ProjectBuilder(String groupId, String artifactId, String version) {
            Model model = new Model();
            model.setModelVersion("4.0.0");
            model.setGroupId(groupId);
            model.setArtifactId(artifactId);
            model.setVersion(version);
            model.setBuild(new Build());
            project = new MavenProject(model);
        }

        public ProjectBuilder setGroupId(String groupId) {
            project.setGroupId(groupId);
            return this;
        }

        public ProjectBuilder setArtifactId(String artifactId) {
            project.setArtifactId(artifactId);
            return this;
        }

        public ProjectBuilder setVersion(String version) {
            project.setVersion(version);
            return this;
        }

        // Dependencies
        //
        public ProjectBuilder addDependency(String groupId, String artifactId, String version, String scope) {
            return addDependency(groupId, artifactId, version, scope, (Exclusion) null);
        }

        public ProjectBuilder addDependency(String groupId, String artifactId, String version, String scope, Exclusion exclusion) {
            return addDependency(groupId, artifactId, version, scope, null, exclusion);
        }

        public ProjectBuilder addDependency(String groupId, String artifactId, String version, String scope, String systemPath) {
            return addDependency(groupId, artifactId, version, scope, systemPath, null);
        }

        public ProjectBuilder addDependency(String groupId, String artifactId, String version, String scope, String systemPath, Exclusion exclusion) {
            Dependency d = new Dependency();
            d.setGroupId(groupId);
            d.setArtifactId(artifactId);
            d.setVersion(version);
            d.setScope(scope);

            if (systemPath != null && scope.equals(Artifact.SCOPE_SYSTEM)) {
                d.setSystemPath(systemPath);
            }

            if (exclusion != null) {
                d.addExclusion(exclusion);
            }

            project.getDependencies().add(d);

            return this;
        }

        // Plugins
        //
        public ProjectBuilder addPlugin(Plugin plugin) {
            project.getBuildPlugins().add(plugin);
            return this;
        }

        public MavenProject get() {
            return project;
        }
    }

    protected static class PluginBuilder {
        private final Plugin plugin;

        public PluginBuilder(String groupId, String artifactId, String version) {
            plugin = new Plugin();
            plugin.setGroupId(groupId);
            plugin.setArtifactId(artifactId);
            plugin.setVersion(version);
        }

        // Dependencies
        //

        public PluginBuilder addDependency(String groupId, String artifactId, String version, String scope, String systemPath) {
            return addDependency(groupId, artifactId, version, scope, systemPath, null);
        }

        public PluginBuilder addDependency(String groupId, String artifactId, String version, String scope, String systemPath, Exclusion exclusion) {
            Dependency d = new Dependency();
            d.setGroupId(groupId);
            d.setArtifactId(artifactId);
            d.setVersion(version);
            d.setScope(scope);

            if (systemPath != null && scope.equals(Artifact.SCOPE_SYSTEM)) {
                d.setSystemPath(systemPath);
            }

            if (exclusion != null) {
                d.addExclusion(exclusion);
            }

            plugin.getDependencies().add(d);

            return this;
        }

        public Plugin get() {
            return plugin;
        }
    }


}
