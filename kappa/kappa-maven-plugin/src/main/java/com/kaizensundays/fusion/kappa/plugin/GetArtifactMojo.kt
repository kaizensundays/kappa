package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.messages.ArtifactResolution
import kotlinx.coroutines.runBlocking
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter

/**
 * Created: Saturday 12/31/2022, 11:32 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "get-artifact", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
class GetArtifactMojo : AbstractKappaMojo() {

    @Parameter(property = "artifact")
    private var artifact = ""

    private fun respond(resolution: ArtifactResolution) {
        val response = runBlocking { nodeClient.post(httpClient(), "http://localhost:7701/handle", resolution) }
        println("*response=$response")
    }

    override fun doExecute() {

        val requestId = System.getProperty("requestId")
        println("*requestId=$requestId")
        println("*artifact=$artifact")

        if (artifact.isBlank()) {
            val resolution = ArtifactResolution(requestId, emptyMap())

            respond(resolution)
        } else {

            val result = artifactManager.resolveArtifact(artifact)

            println("file=${result.artifact.file} exist=${result.artifact.file.exists()}")

            val file = result.artifact.file.canonicalPath

            val resolution = ArtifactResolution(requestId, mapOf(artifact to file))

            respond(resolution)
        }

    }

}