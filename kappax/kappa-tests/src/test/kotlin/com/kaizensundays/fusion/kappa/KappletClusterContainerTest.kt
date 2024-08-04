package com.kaizensundays.fusion.kappa

import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.Volume
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.Executors

/**
 * Created: Sunday 8/4/2024, 12:06 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Tag("container-test")
class KappletClusterContainerTest {

    companion object {

        private val logger: Logger = LoggerFactory.getLogger(KappletClusterContainerTest::class.java)

        private const val ATOMIX_PROFILE = "Consensus"
        private const val ATOMIX_BOOTSTRAP = "A:kapplet1:5001;B:kapplet2:5002;C:kapplet3:5003"
        private const val KAPPLET_PROPERTIES = "kapplet.yml"

        private const val COMMAND_PORT = 7701
        private const val KUBE_HOST = "Nevada"

        private val env = mapOf(
            0 to mutableMapOf(
                "ATOMIX_PROFILE" to ATOMIX_PROFILE,
                "ATOMIX_BOOTSTRAP" to ATOMIX_BOOTSTRAP,
                "ATOMIX_NODE_ID" to "A",
                "ATOMIX_NODE_HOST" to "kapplet1",
                "ATOMIX_NODE_PORT" to "5001",
                "KAPPLET_SERVER_PORT" to "7711",
                "KAPPLET_WEB_PORT" to "7713",
                "KAPPLET_PROPERTIES" to KAPPLET_PROPERTIES
            ),
            1 to mutableMapOf(
                "ATOMIX_PROFILE" to ATOMIX_PROFILE,
                "ATOMIX_BOOTSTRAP" to ATOMIX_BOOTSTRAP,
                "ATOMIX_NODE_ID" to "B",
                "ATOMIX_NODE_HOST" to "kapplet2",
                "ATOMIX_NODE_PORT" to "5002",
                "KAPPLET_SERVER_PORT" to "7721",
                "KAPPLET_WEB_PORT" to "7723",
                "KAPPLET_PROPERTIES" to KAPPLET_PROPERTIES
            ),
            2 to mutableMapOf(
                "ATOMIX_PROFILE" to ATOMIX_PROFILE,
                "ATOMIX_BOOTSTRAP" to ATOMIX_BOOTSTRAP,
                "ATOMIX_NODE_ID" to "C",
                "ATOMIX_NODE_HOST" to "kapplet3",
                "ATOMIX_NODE_PORT" to "5003",
                "KAPPLET_SERVER_PORT" to "7711",
                "KAPPLET_WEB_PORT" to "7713",
                "KAPPLET_PROPERTIES" to KAPPLET_PROPERTIES
            ),
        )

        private const val IMAGE = "localhost:32000/kappa:latest"

        private val containers: List<GenericContainer<*>> = List(3) {
            GenericContainer<Nothing>(DockerImageName.parse(IMAGE))
        }

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

            val executor = Executors.newFixedThreadPool(3)

            //val network = Network.builder().id("kappa").build()
            val network = Network.newNetwork()

            containers.forEachIndexed { n, container ->
                executor.execute {
                    val name = env[n]?.get("ATOMIX_NODE_HOST")
                    container.withNetwork(network)
                        .withExposedPorts(COMMAND_PORT)
                        .withExtraHost(KUBE_HOST, kubeIp)
                        .withEnv(env[n])
                        .waitingFor(Wait.forHttp("/ping"))
                        .withCreateContainerCmdModifier { cmd ->
                            cmd.withName(name)
                            cmd.hostConfig?.withBinds(Bind("/home/super/var/shared/m2", Volume("/opt/m2")))
                        }
                    container.start()
                    logger.info("Stared - $name")
                }
            }
        }

        @JvmStatic
        @AfterAll
        fun afterClass() {
            Thread.sleep(1_000)
            containers.forEach { container -> container.stop() }
        }

    }

    @Test
    fun test() {
        Thread.sleep(30_000)
    }

}