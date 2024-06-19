package com.kaizensundays.fusion.kappa

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.Volume
import com.kaizensundays.fusion.kappa.core.Deployments
import com.kaizensundays.fusion.kappa.core.api.*
import com.kaizensundays.fusion.ktor.KtorProducer
import com.kaizensundays.fusion.messaging.DefaultLoadBalancer
import com.kaizensundays.fusion.messaging.Instance
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.lang.Thread.sleep
import java.net.URI
import java.time.Duration

/**
 * Created: Sunday 6/2/2024, 12:57 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Tag("container-test")
class KappletContainerTest {

    companion object {
        private const val SERVER_PORT = 7701

        private const val IMAGE = "localhost:32000/kappa:latest"

        private val container: GenericContainer<*> = GenericContainer<Nothing>(DockerImageName.parse(IMAGE))

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            container.withExposedPorts(SERVER_PORT)
                .waitingFor(Wait.forHttp("/ping"))
                .withCreateContainerCmdModifier { cmd ->
                    cmd.withName("kapplet-test")
                    cmd.hostConfig?.withBinds(Bind("/home/super/var/shared/m2", Volume("/opt/m2")))
                }
            container.start()
        }

        @JvmStatic
        @AfterAll
        fun afterClass() {
            sleep(1_000)
            container.stop()
        }

    }

    private val jsonConverter = ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .registerKotlinModule()

    private fun renderVersion(version: String, serviceMap: Map<String, Service>) {
        serviceMap.forEach { (_, service) ->
            service.artifact = service.artifact?.replace("""%%version%%""", version)
        }
    }

    private fun KtorProducer.executeGet(): GetResponse {

        val body = jsonConverter.writeValueAsString(GetRequest())

        val bytes = this.request(URI("post:/handle"), body.toByteArray())
            .blockLast(Duration.ofSeconds(30))

        val json = if (bytes != null) String(bytes) else "?"
        println(json)

        return jsonConverter.readValue(json, GetResponse::class.java)
    }

    private fun KtorProducer.executeApply(fileName: String, version: String): ApplyResponse {

        val deployments = Deployments()

        val serviceMap = runBlocking { deployments.readAndValidateDeployment(fileName) }

        renderVersion(version, serviceMap)

        val request = Apply(serviceMap)

        val body = jsonConverter.writeValueAsString(request)

        val bytes = this.request(URI("post:/handle"), body.toByteArray())
            .blockLast(Duration.ofSeconds(30))

        val json = if (bytes != null) String(bytes) else "?"
        println(json)

        return jsonConverter.readValue(json, ApplyResponse::class.java)
    }

    private fun KtorProducer.executeStop(serviceId: String): String {

        val bytes = this.request(URI("get:/stop/$serviceId"))
            .blockLast(Duration.ofSeconds(30))

        val json = if (bytes != null) String(bytes) else "?"
        println(json)

        return json
    }

    @Test
    fun getReturnsOk() {

        val port = container.getMappedPort(SERVER_PORT)
        println("port=$port")

        val instance = Instance("Nevada", port)

        val producer = KtorProducer(DefaultLoadBalancer(listOf(instance)))

        var response = producer.executeGet()
        assertEquals(0, response.code)
        assertEquals(0, response.services.size)

        sleep(1_000)

        val applyResponse = producer.executeApply("easybox.yaml", "0.0.0-SNAPSHOT")
        assertEquals(0, applyResponse.code)

        sleep(1_000)

        response = producer.executeGet()
        assertEquals(0, response.code)
        assertEquals(1, response.services.size)

        sleep(1_000)

/*
        producer.executeStop("easybox")

        sleep(1_000)
*/
    }

}