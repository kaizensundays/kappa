package com.kaizensundays.fusion.kappa

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kaizensundays.fusion.kappa.core.api.GetRequest
import com.kaizensundays.fusion.kappa.core.api.GetResponse
import com.kaizensundays.fusion.ktor.KtorProducer
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

}