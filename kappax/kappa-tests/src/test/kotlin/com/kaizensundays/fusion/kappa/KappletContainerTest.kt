package com.kaizensundays.fusion.kappa

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.Volume
import com.kaizensundays.fusion.kappa.core.api.GetRequest
import com.kaizensundays.fusion.kappa.core.api.GetResponse
import com.kaizensundays.fusion.ktor.KtorProducer
import com.kaizensundays.fusion.messaging.DefaultLoadBalancer
import com.kaizensundays.fusion.messaging.Instance
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
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

    @Test
    fun getReturnsOk() {
        sleep(3_000)

        val port = container.getMappedPort(7701)
        println("port=$port")

        val instance = Instance("192.168.0.19", port)

        val producer = KtorProducer(DefaultLoadBalancer(listOf(instance)))

        val body = jsonConverter.writeValueAsString(GetRequest())

        val bytes = producer.request(URI("post:/handle"), body.toByteArray())
            .blockLast(Duration.ofSeconds(30))

        val json = if (bytes != null) String(bytes) else "?"
        println(json)

        val response = jsonConverter.readValue(json, GetResponse::class.java)
        assertEquals(0, response.code)
        assertEquals(0, response.services.size)

        sleep(3_000)
    }

}