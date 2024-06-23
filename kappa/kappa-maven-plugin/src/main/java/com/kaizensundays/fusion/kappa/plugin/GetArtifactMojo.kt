package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.core.api.ArtifactResolution
import com.kaizensundays.fusion.ktor.KtorProducer
import com.kaizensundays.fusion.messaging.DefaultLoadBalancer
import com.kaizensundays.fusion.messaging.Instance
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import java.net.URI
import java.time.Duration

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

        val instance = Instance("localhost", 7701)
        val producer = KtorProducer(DefaultLoadBalancer(listOf(instance)))

        val request = jsonConverter.fromObject(resolution)

        val response = producer.request(URI("post:/handle"), request.toByteArray())
            .blockLast(Duration.ofSeconds(30))

        val json = if (response != null) String(response) else "?"

        println("*** response=$json")
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

            println("*** resolution=$resolution")

            respond(resolution)
        }

    }

}