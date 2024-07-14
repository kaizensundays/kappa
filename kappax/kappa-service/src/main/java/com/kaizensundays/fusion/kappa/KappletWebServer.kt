package com.kaizensundays.fusion.kappa

import com.kaizensundays.fusion.kappa.service.Kapplet
import com.kaizensundays.fusion.kappa.web.WebController
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.defaultResource
import io.ktor.server.http.content.resource
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticBasePackage
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.webjars.Webjars
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created: Saturday 7/13/2024, 12:13 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KappletWebServer(
    private val port: Int,
    private val kapplet: Kapplet
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val controller = WebController()

    private var engine: ApplicationEngine? = null

    private fun startServer() {
        embeddedServer(CIO, port = this.port, watchPaths = listOf("classes", "resources")) {
            install(Webjars)
            routing {
                static("/") {
                    staticBasePackage = "static"
                    resource("index.html")
                    defaultResource("index.html")
                }
                get("/ping") {
                    call.respondText("Web: Ok")
                }
                get("/get") {
                    val services = kapplet.getServices()
                    val html = controller.render(services)
                    call.respondText { html }
                }
                get("/empty") {
                    call.respondText { "" }
                }
            }
        }.start(wait = true)
    }

    fun start() {
        Thread({ startServer() }, "web-start").start()

        logger.info("Started")
    }

    fun stop() {
        engine?.stop(3000L, 7000L)

        logger.info("Stopped")
    }

}