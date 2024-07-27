package com.kaizensundays.fusion.kappa

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kaizensundays.fusion.kappa.messages.MsgFrame
import com.kaizensundays.fusion.kappa.web.WebController
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.defaultResource
import io.ktor.server.http.content.resource
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticBasePackage
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.webjars.Webjars
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

/**
 * Created: Saturday 7/13/2024, 12:13 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KappletWebServer(
    private val port: Int,
    private val controller: WebController
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val jackson = ObjectMapper().registerKotlinModule()

    private var engine: ApplicationEngine? = null

    private fun startServer() {
        embeddedServer(CIO, port = this.port, watchPaths = listOf("classes", "resources")) {
            install(Webjars)
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
            routing {
                static("/") {
                    staticBasePackage = "static"
                    resource("index.html")
                    resource("kappa.css")
                    defaultResource("index.html")
                }
                get("/ping") {
                    call.respondText("Web: Ok")
                }
                get("/getServices") {
                    val html = controller.renderServices()
                    call.respondText { html }
                }
                get("/initServices") {
                    val html = controller.clearServices()
                    call.respondText { html }
                }
                post("/stopService") {
                    val wire = call.receive<String>()
                    println(wire)
                    val frame = jackson.readValue(wire, MsgFrame::class.java)
                    println(frame)
                    delay(3.seconds)
                    call.respondText("Ok")
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