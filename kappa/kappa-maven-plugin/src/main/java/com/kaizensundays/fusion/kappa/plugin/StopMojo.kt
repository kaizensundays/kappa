package com.kaizensundays.fusion.kappa.plugin

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.runBlocking
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

/**
 * Created: Sunday 1/29/2023, 11:23 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "stop", defaultPhase = LifecyclePhase.NONE)
open class StopMojo : AbstractKappaMojo() {

    suspend fun get(client: HttpClient, url: String = ""): String {
        val response: HttpResponse = client.get(url)
        return response.status.toString()
    }

    open fun urlPath(serviceId: String = ""): String {
        if (serviceId.isBlank()) {
            throw IllegalArgumentException()
        }
        return "/stop/$serviceId"
    }

    private fun url(port: Int, serviceId: String = ""): String {
        return "http://localhost:$port/" + urlPath(serviceId)
    }

    override fun doExecute() {

        val props = loadProperties("application.properties")
        val port = props.getPropertyAsInt("kapplet.server.port")

        val serviceId = System.getProperty("id", "")

        if (kappletIsNotRunning(port)) {
            println("Kapplet is not running")
            return
        }

        val httpClient = httpClient()

        runBlocking {
            try {
                val url = url(port, serviceId)
                val status = get(httpClient, url)
                println(status)
                println("Service $serviceId stopped")
            } catch (e: Exception) {
                println(e.message)
            }
        }

    }

}