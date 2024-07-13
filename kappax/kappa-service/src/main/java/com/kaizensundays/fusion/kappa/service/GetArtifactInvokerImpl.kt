package com.kaizensundays.fusion.kappa.service

import com.kaizensundays.fusion.kappa.maven.api.GetArtifactInvoker
import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Created: Sunday 5/26/2024, 12:33 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class GetArtifactInvokerImpl : GetArtifactInvoker {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun execute(requestId: String, artifact: String) {
        logger.info("execute >")

        val mavenHome = System.getenv()["M2_HOME"]
        System.setProperty("maven.home", mavenHome ?: "")

        val request = DefaultInvocationRequest()
        request.goals = listOf("com.kaizensundays.fusion.kappa:kappa-maven-plugin:0.0.0-SNAPSHOT:get-artifact");
        request.properties = mapOf("requestId" to requestId, "artifact" to artifact).toProperties()
        request.baseDirectory = File(".");
        request.timeoutInSeconds = 10

        val invoker = DefaultInvoker()
        val result = invoker.execute(request)

        logger.info("execute < {}", result)
    }

}