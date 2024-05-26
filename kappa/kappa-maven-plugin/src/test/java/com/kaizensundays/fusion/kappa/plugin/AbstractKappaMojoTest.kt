package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.core.api.JacksonSerializable
import com.kaizensundays.fusion.kappa.messages.JacksonObjectConverter
import com.kaizensundays.fusion.kappa.core.api.Service
import com.kaizensundays.fusion.kappa.core.api.unsupportedOperation
import com.kaizensundays.fusion.messaging.Instance
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.junit.jupiter.api.Test
import org.springframework.util.SocketUtils
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Sunday 12/4/2022, 11:18 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
typealias ClientContentNegotiation = io.ktor.client.plugins.contentnegotiation.ContentNegotiation

@Suppress("ExtractKtorModule")
class AbstractKappaMojoTest {

    private val jsonConverter = JacksonObjectConverter<JacksonSerializable>()

    private val mojo = object : AbstractKappaMojo() {
        override fun doExecute() {
            unsupportedOperation()
        }
    }

    @Test
    fun getConfiguration() {

        val mojoConf = "mojo-test.properties"

        var conf = mojo.getConfiguration(mojoConf, mutableMapOf())
        assertEquals("[Instance(host=hostA, port=7701), Instance(host=hostB, port=7703), Instance(host=hostC, port=7707)]", conf.hosts.toString())

        conf = mojo.getConfiguration(mojoConf, mutableMapOf("KAPPA_HOSTS" to "hostD:7701,hostE:7703"))
        assertEquals("[Instance(host=hostD, port=7701), Instance(host=hostE, port=7703)]", conf.hosts.toString())
    }

    @Test
    fun getKappletReturnsOk() {

        val port = SocketUtils.findAvailableTcpPort(50_000, 59_000)

        val service = Service("kapplet", pid = 1)
        val json = jsonConverter.fromObjects(service)

        val server = KtorEmbeddedServer(port)
            .set {
                routing {
                    get("/get-kapplet") {
                        call.respond(json)
                    }
                }
            }

        assertTrue(server.start(30, TimeUnit.SECONDS))

        val instance = Instance("localhost", port)
        val kapplet = mojo.getKapplet(instance, retries = 1)
        assertEquals(1, kapplet.pid)
    }

    @Test
    fun getKappletReturnsNotFound() {
    }

}