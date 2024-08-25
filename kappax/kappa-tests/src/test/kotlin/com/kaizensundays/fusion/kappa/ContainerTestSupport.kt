package com.kaizensundays.fusion.kappa

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kaizensundays.fusion.kappa.core.Deployments
import com.kaizensundays.fusion.kappa.core.api.Apply
import com.kaizensundays.fusion.kappa.core.api.ApplyResponse
import com.kaizensundays.fusion.kappa.core.api.GetRequest
import com.kaizensundays.fusion.kappa.core.api.GetResponse
import com.kaizensundays.fusion.kappa.core.api.Service
import com.kaizensundays.fusion.ktor.KtorProducer
import kotlinx.coroutines.runBlocking
import java.net.URI
import java.time.Duration

/**
 * Created: Saturday 8/24/2024, 12:24 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class ContainerTestSupport {


    val jsonConverter = ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .registerKotlinModule()

    fun KtorProducer.executeGet(): GetResponse {

        val body = jsonConverter.writeValueAsString(GetRequest())

        val bytes = this.request(URI("post:/handle"), body.toByteArray())
            .blockLast(Duration.ofSeconds(30))

        val json = if (bytes != null) String(bytes) else "?"
        println(json)

        return jsonConverter.readValue(json, GetResponse::class.java)
    }

    private fun renderVersion(version: String, serviceMap: Map<String, Service>) {
        serviceMap.forEach { (_, service) ->
            service.artifact = service.artifact?.replace("""%%version%%""", version)
        }
    }

    fun KtorProducer.executeApply(fileName: String, version: String): ApplyResponse {

        val deployments = Deployments()

        val serviceMap = runBlocking { deployments.readAndValidateDeployment(fileName) }

        renderVersion(version, serviceMap)

        val request = Apply(serviceMap)

        val body = jsonConverter.writeValueAsString(request)

        val bytes = this.request(URI("post:/handle"), body.toByteArray())
            .blockLast(Duration.ofSeconds(100))

        val json = if (bytes != null) String(bytes) else "?"
        println(json)

        return jsonConverter.readValue(json, ApplyResponse::class.java)
    }

    fun KtorProducer.executeStop(serviceId: String): String {

        val bytes = this.request(URI("get:/stop/$serviceId"))
            .blockLast(Duration.ofSeconds(30))

        val json = if (bytes != null) String(bytes) else "?"
        println(json)

        return json
    }

}