package com.kaizensundays.fusion.kappa.plugin

import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import org.junit.jupiter.api.Test
import java.io.File


/**
 * Created: Sunday 6/25/2023, 12:52 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class InvokeMojoRemoteTest {

    @Test
    fun withInvoker() {

        val mavenHome = System.getenv()["M2_HOME"]
        System.setProperty("maven.home", mavenHome ?: "")

        val request = DefaultInvocationRequest()
        request.goals = listOf("com.kaizensundays.fusion:kappa-maven-plugin:0.0.0-SNAPSHOT:get-artifact");
        request.baseDirectory = File(".")
        request.properties = mapOf("requestId" to "123").toProperties()

        val invoker = DefaultInvoker()
        val result = invoker.execute(request)

        println(result.toString())
    }

}