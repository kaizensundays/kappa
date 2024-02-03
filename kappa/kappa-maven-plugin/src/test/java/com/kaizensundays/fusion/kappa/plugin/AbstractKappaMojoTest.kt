package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.service.Service
import com.kaizensundays.fusion.kappa.unsupportedOperation
import com.kaizensundays.fusion.messaging.Instance
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.springframework.util.SocketUtils
import java.util.concurrent.CountDownLatch
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

        val latch = CountDownLatch(1)

        val port = SocketUtils.findAvailableTcpPort(50_000, 59_000)

        val server = embeddedServer(CIO, port, configure = {
        }) {
            install(Routing)
            install(StatusPages) {
                exception<Throwable> { call, e ->
                    call.respondText(text = "500: $e", status = HttpStatusCode.InternalServerError)
                }
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
            routing {
                get("/get-kapplet") {
                    call.respond(Service("kapplet", pid = 1))
                }
            }
        }

        server.environment.monitor.subscribe(ApplicationStarted) {
            latch.countDown()
        }

        server.start(false)

        assertTrue(latch.await(30, TimeUnit.SECONDS))

        val instance = Instance("localhost", port)
        val kapplet = mojo.getKapplet(instance, retries = 1)
        assertEquals(1, kapplet.pid)
    }

    @Test
    fun getKappletReturnsNotFound() {
    }

}