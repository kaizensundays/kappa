package com.kaizensundays.fusion.kappa

import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.Volume
import com.kaizensundays.fusion.ktor.KtorProducer
import com.kaizensundays.fusion.messaging.DefaultLoadBalancer
import com.kaizensundays.fusion.messaging.Instance
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.lang.Thread.sleep
import java.net.InetAddress
import java.net.UnknownHostException

/**
 * Created: Sunday 6/2/2024, 12:57 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Tag("container-test")
class KappletContainerTest : ContainerTestSupport() {

    companion object {
        private const val SERVER_PORT = 7701

        private const val IMAGE = "localhost:32000/kappa:latest"

        private const val KUBE_HOST = "Nevada"

        private val container: GenericContainer<*> = GenericContainer<Nothing>(DockerImageName.parse(IMAGE))

        @JvmStatic
        fun resolve(hostname: String): String {
            return try {
                InetAddress.getByName(hostname).hostAddress
            } catch (e: UnknownHostException) {
                throw RuntimeException(e)
            }
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            val kubeIp = resolve(KUBE_HOST)
            container.withExposedPorts(SERVER_PORT)
                .withExtraHost(KUBE_HOST, kubeIp)
                .withEnv(
                    mutableMapOf(
                        "ATOMIX_PROFILE" to "Consensus",
                        "ATOMIX_BOOTSTRAP" to "SINGLE:localhost:5501",
                        "ATOMIX_NODE_HOST" to "localhost",
                        "ATOMIX_NODE_PORT" to "5501",
                        "ATOMIX_NODE_ID" to "SINGLE",
                        "KAPPLET_SERVER_PORT" to "7701",
                        "KAPPLET_WEB_PORT" to "7703",
                        "KAPPLET_PROPERTIES" to "kapplet.yml"
                    )
                )
                .waitingFor(Wait.forHttp("/ping"))
                .withCreateContainerCmdModifier { cmd ->
                    cmd.withName("kapplet")
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

    @Test
    fun startAndStopEasyBox() {

        val port = container.getMappedPort(SERVER_PORT)
        println("port=$port")

        val instance = Instance(KUBE_HOST, port)

        val producer = KtorProducer(DefaultLoadBalancer(listOf(instance)))

        var response = producer.executeGet()
        assertEquals(0, response.code)
        assertEquals(0, response.services.size)

        sleep(10_000)

        val applyResponse = producer.executeApply("easybox.yaml", "0.0.0-SNAPSHOT")
        assertEquals(0, applyResponse.code)

        sleep(10_000)

        response = producer.executeGet()
        assertEquals(0, response.code)
        assertEquals(1, response.services.size)

        sleep(10_000)

        producer.executeStop("easybox")

        sleep(1_000)

        response = producer.executeGet()
        assertEquals(0, response.code)
        assertEquals(0, response.services.size)

        sleep(1_000)
    }

}