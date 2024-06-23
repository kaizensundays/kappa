package com.kaizensundays.fusion.kappa.plugin

import org.apache.maven.AbstractCoreMavenComponentTestSupport
import org.apache.maven.execution.DefaultMavenExecutionRequest
import org.apache.maven.execution.DefaultMavenExecutionResult
import org.apache.maven.execution.MavenExecutionRequest
import org.apache.maven.execution.MavenExecutionResult
import org.apache.maven.execution.MavenSession
import org.apache.maven.model.Plugin
import org.apache.maven.plugin.BuildPluginManager
import org.apache.maven.plugin.MojoExecution
import org.apache.maven.plugin.descriptor.MojoDescriptor
import org.apache.maven.project.MavenProject
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory
import org.eclipse.aether.repository.LocalRepository
import org.junit.jupiter.api.Test

/**
 * Created: Tuesday 7/4/2023, 12:56 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class InvokeMojoWithCoreRemoteTest : AbstractCoreMavenComponentTestSupport() {

    override fun getProjectsDirectory(): String {
        return "."
    }

    @Test
    fun withCore() {

        val project = MavenProject()
        project.groupId = "com.kaizensundays.fusion"
        project.artifactId = "kappa-service"
        project.version = "0.0.0-SNAPSHOT"
        project.remoteArtifactRepositories = emptyList()
        project.pluginArtifactRepositories = emptyList()

        val pluginManager = container.lookup(BuildPluginManager::class.java)
        val localRepo = LocalRepository("""C:\super\m2n""")
        val repositorySystemSession = MavenRepositorySystemUtils.newSession()
        repositorySystemSession.localRepositoryManager = SimpleLocalRepositoryManagerFactory().newInstance(repositorySystemSession, localRepo)

        val plugin = Plugin()
        plugin.groupId = "com.kaizensundays.fusion.kappa"
        plugin.artifactId = "kappa-maven-plugin"
        plugin.version = "0.0.0-SNAPSHOT"

        val pluginDescriptor = pluginManager.loadPlugin(plugin, emptyList(), repositorySystemSession)

        val mojoDescriptor = MojoDescriptor()
        mojoDescriptor.pluginDescriptor = pluginDescriptor
        mojoDescriptor.goal = ""

        val mojoExecution = MojoExecution(mojoDescriptor)

        val req: MavenExecutionRequest = DefaultMavenExecutionRequest()
        val res: MavenExecutionResult = DefaultMavenExecutionResult()

        val session = MavenSession(container, repositorySystemSession, req, res)
        session.projects = listOf(project)

        val realm = pluginManager.getPluginRealm(session, pluginDescriptor)

        pluginManager.executeMojo(session, mojoExecution)
    }

}