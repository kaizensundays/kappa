package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Service
import org.apache.maven.artifact.handler.ArtifactHandler
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.project.DefaultProjectBuildingRequest
import org.apache.maven.project.ProjectBuildingRequest
import org.apache.maven.repository.RepositorySystem
import org.apache.maven.shared.transfer.artifact.ArtifactCoordinate
import org.apache.maven.shared.transfer.artifact.DefaultArtifactCoordinate
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResolver
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResult
import org.apache.maven.shared.transfer.dependencies.DefaultDependableCoordinate
import org.apache.maven.shared.transfer.dependencies.DependableCoordinate
import org.codehaus.plexus.util.StringUtils
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.repository.RepositoryPolicy
import java.lang.reflect.Field

/**
 * Created: Sunday 2/5/2023, 12:23 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@SuppressWarnings("kotlin:S6518") // Element access should use indexed access operators
class DefaultArtifactManager(
    private val pluginContext: MutableMap<Any?, Any?>,
    private val session: MavenSession,
    private val artifactResolver: ArtifactResolver,
    private val artifactHandlerManager: ArtifactHandlerManager,
    private val repositorySystem: RepositorySystem,
    private val pomRemoteRepositories: List<ArtifactRepository>
) : ArtifactManager {

    override fun getProjectVersion(): Version {

        val descriptor = this.pluginContext["pluginDescriptor"]
                as? PluginDescriptor ?: throw IllegalStateException()

        return Version(descriptor.version)
    }

    private fun toArtifactCoordinate(dependableCoordinate: DependableCoordinate): ArtifactCoordinate {
        val handler: ArtifactHandler = artifactHandlerManager.getArtifactHandler(dependableCoordinate.type)
        val coordinate = DefaultArtifactCoordinate()
        coordinate.groupId = dependableCoordinate.groupId
        coordinate.artifactId = dependableCoordinate.artifactId
        coordinate.version = dependableCoordinate.version
        coordinate.classifier = dependableCoordinate.classifier
        coordinate.extension = handler.extension
        return coordinate
    }

    fun setPrivateField(name: String, value: Any, obj: Any): Field {
        val field = obj.javaClass.getDeclaredField(name)
        field.isAccessible = true
        field.set(obj, value)
        return field
    }

    private fun parseCoordinates(artifact: String): DependableCoordinate {

        val coordinate = DefaultDependableCoordinate()

        val tokens = StringUtils.split(artifact, ":")
        if (tokens.size < 3 || tokens.size > 5) {
            throw MojoFailureException(
                "Invalid artifact, you must specify "
                        + "groupId:artifactId:version[:packaging[:classifier]] " + artifact
            )
        }
        coordinate.groupId = tokens[0]
        coordinate.artifactId = tokens[1]
        coordinate.version = tokens[2]
        if (tokens.size >= 4) {
            coordinate.type = tokens[3]
        }
        if (tokens.size == 5) {
            coordinate.classifier = tokens[4]
        }

        return coordinate
    }

    override fun resolveArtifact(artifact: String): ArtifactResult {

        val repositorySession = session.repositorySession
        if (repositorySession is DefaultRepositorySystemSession) {
            setPrivateField("updatePolicy", RepositoryPolicy.UPDATE_POLICY_ALWAYS, repositorySession)
        }

        val coordinate = parseCoordinates(artifact)

        val repoList: MutableList<ArtifactRepository> = ArrayList()

        repoList.addAll(pomRemoteRepositories)

        val buildingRequest: ProjectBuildingRequest = DefaultProjectBuildingRequest(session.projectBuildingRequest)

        val settings = session.settings
        repositorySystem.injectMirror(repoList, settings.mirrors)
        repositorySystem.injectProxy(repoList, settings.proxies)
        repositorySystem.injectAuthentication(repoList, settings.servers)

        buildingRequest.remoteRepositories = repoList

        return artifactResolver.resolveArtifact(buildingRequest, toArtifactCoordinate(coordinate));
    }

    override fun resolveArtifacts(serviceMap: Map<String, Service>, artifactResolver: (String) -> String): Map<String, String> {

        return serviceMap.values
            .mapNotNull { service -> service.artifact }
            .filter { artifact -> artifact.isNotBlank() }
            .map { artifact -> artifact to artifactResolver.invoke(artifact) }
            .toMap()
    }

}