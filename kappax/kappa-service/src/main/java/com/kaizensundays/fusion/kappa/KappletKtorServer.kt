package com.kaizensundays.fusion.kappa

import com.kaizensundays.fusion.kappa.core.api.Apply
import com.kaizensundays.fusion.kappa.core.api.Event
import com.kaizensundays.fusion.kappa.messages.JacksonObjectConverter
import com.kaizensundays.fusion.kappa.os.Os
import com.kaizensundays.fusion.kappa.service.Kapplet
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created: Saturday 10/1/2022, 1:03 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KappletKtorServer(
    private val port: Int,
    private val os: Os,
    private val service: Kapplet
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val converter = JacksonObjectConverter<Event>()

    private var engine: ApplicationEngine? = null

    private fun startServer() {

        embeddedServer(CIO, port = this.port, configure = {
        }) {
            install(CORS) {
                anyHost()
            }
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
                get("/ping") {
                    call.respondText("Kapplet: Ok " + System.currentTimeMillis())
                }
                get("/test") {
                    call.respond(service.test())
                }
                get("/get") {
                    call.respond(service.getServices())
                }
                get("/get-kapplet") {
                    call.respond(service.getKapplet())
                }
                get("/get-nodes") {
                    call.respond(service.getNodes())
                }
                get("/stop-kapplet") {
                    call.respond(service.stopKapplet())
                }
                get("/stop/{serviceId}") {
                    val serviceId: String = call.parameters["serviceID"] ?: ""
                    service.stopService(serviceId)
                    call.respond(HttpStatusCode.OK)
                }
                get("/get/pid/{serviceId}") {
                    val serviceId = call.parameters["serviceID"]
                    if (serviceId != null) {
                        val pid = os.findPID(serviceId)
                        call.respondText("Ok pid=$pid")
                    }
                    call.respondText("Error")
                }
                get("/shutdown/pid={pid}") {
                    val sPid = call.parameters["pid"]
                    if (sPid != null && sPid.isNotBlank()) {
                        val pid = Integer.parseInt(sPid)
                        val result = os.shutdown(pid)
                        call.respondText("Ok result=$result")
                    }
                    call.respondText("Error")
                }
                post("/apply") {
                    val apply = call.receive<Apply>()
                    val result = service.apply(apply)
                    println(result)
                    call.respondText(result)
                }
                post("/handle") {
                    val wire = call.receive<String>()
                    val result = service.handle(wire)
                    call.respondText(result)
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