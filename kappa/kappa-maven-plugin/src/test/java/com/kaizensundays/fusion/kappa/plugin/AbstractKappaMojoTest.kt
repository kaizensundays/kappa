package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.service.Service
import com.kaizensundays.fusion.kappa.unsupportedOperation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

/**
 * Created: Sunday 12/4/2022, 11:18 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
typealias ClientContentNegotiation = io.ktor.client.plugins.contentnegotiation.ContentNegotiation

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

        testApplication {
            install(ContentNegotiation) {
                json()
            }
            routing {
                get("/get-kapplet") {
                    call.respond(Service("kapplet", pid = 1))
                }
            }

            val client = createClient {
                install(ClientContentNegotiation) {
                    json()
                }
            }

            val kapplet = mojo.getKapplet(client, retries = 1)
            assertEquals(1, kapplet.pid)
        }
    }

    @Test
    fun getKappletReturnsNotFound() {

        testApplication {
            routing { }

            assertThrows<RuntimeException> { mojo.getKapplet(client, retries = 1) }
        }
    }

}