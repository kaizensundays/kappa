package com.kaizensundays.fusion.kappa

import com.kaizensundays.fusion.kappa.event.ResponseCode
import com.kaizensundays.fusion.kappa.messages.ApplyResponse
import com.kaizensundays.fusion.kappa.messages.ArtifactResolution
import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 7/2/2023, 7:41 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ApplyHandler(
    private val artifactResolutionPendingResults: PendingResults<ArtifactResolution>
) : AbstractHandler<Apply, ApplyResponse>() {

    fun getArtifacts(serviceMap: Map<String, Service>): List<String> {
        return serviceMap.values.mapNotNull { service -> service.artifact }
    }

    private fun resolveArtifact(requestId: String, artifact: String) {
        logger.info("resolveArtifact >")

        val mavenHome = System.getenv()["M2_HOME"]
        System.setProperty("maven.home", mavenHome ?: "")

        val request = DefaultInvocationRequest()
        request.goals = listOf("com.kaizensundays.fusion:kappa-maven-plugin:0.0.0-SNAPSHOT:get-artifact");
        request.properties = mapOf("requestId" to requestId, "artifact" to artifact).toProperties()
        request.baseDirectory = File(".");
        request.timeoutInSeconds = 10

        val invoker = DefaultInvoker()
        val result = invoker.execute(request)

        logger.info("resolveArtifact < {}", result)
    }

    override fun doHandle(request: Apply): ApplyResponse {
        logger.info("< $request")

        val requestId = UUID.randomUUID().toString()
        val pendingResult = artifactResolutionPendingResults.get(requestId)

        val artifacts = getArtifacts(request.serviceMap)

        val artifact = artifacts.first()

        resolveArtifact(requestId, artifact)

        val resolution = pendingResult.get(60, TimeUnit.SECONDS)
        logger.info("resolution=$resolution")

        if (resolution == null) {
            return request.response(ResponseCode.Timeout)
        }

        return request.response(ResponseCode.Ok)
    }

}