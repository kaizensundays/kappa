package com.kaizensundays.fusion.kappa.plugin

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created: Saturday 2/3/2024, 5:51 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Suppress("ExtractKtorModule")
class KtorEmbeddedServer(private val port: Int) {

    private lateinit var server: CIOApplicationEngine

    private val modules: MutableList<Application.() -> Unit> = mutableListOf()

    fun set(module: Application.() -> Unit): KtorEmbeddedServer {
        this.modules.add(module)
        return this
    }

    fun start(timeout: Long, unit: TimeUnit): Boolean {

        val latch = CountDownLatch(1)

        server = embeddedServer(CIO, port, configure = {
        }) {
            install(Routing)
            install(StatusPages) {
                exception<Throwable> { call, e ->
                    call.respondText(text = "500: $e", status = HttpStatusCode.InternalServerError)
                }
            }
            modules.forEach { module -> module.invoke(this) }
        }

        server.environment.monitor.subscribe(ApplicationStarted) {
            latch.countDown()
        }

        server.start(false)

        return latch.await(timeout, unit)
    }

}