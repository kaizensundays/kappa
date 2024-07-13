package com.kaizensundays.fusion.kappa

import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created: Sunday 9/4/2022, 1:40 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KtorServer(private val port: Int) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private var engine: ApplicationEngine? = null

    private fun startServer() {

        embeddedServer(CIO, port = this.port, configure = {
        }) {
            install(CORS) {
                anyHost()
            }
            install(Routing)
            routing {
                get("/ping") {
                    call.respondText("Ok " + System.currentTimeMillis())
                }
            }
        }.start(wait = true)

    }

    @PostConstruct
    fun start() {

        Thread({ startServer() }, "ktor-start").start()

        logger.info("Started")
    }

    @PreDestroy
    fun stop() {
        engine?.stop(3000L, 7000L)

        logger.info("Stopped")
    }

}