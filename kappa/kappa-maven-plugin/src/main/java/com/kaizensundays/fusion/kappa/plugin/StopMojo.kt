package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Kappa
import com.kaizensundays.fusion.messaging.Instance
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

    open fun urlPath(serviceId: String): String {
        require(serviceId.isNotBlank())
        return "/stop/$serviceId"
    }

    private fun url(instance: Instance, serviceId: String = ""): String {
        return "http://${instance.host}:${instance.port}/" + urlPath(serviceId)
    }

    override fun doExecute() {

        val conf = getConfiguration()
        println("$conf\n")

        val hostPort = conf.hosts.first()

        if (kappletIsNotRunning(hostPort)) {
            println("Kapplet is not running")
            return
        }

        val serviceId = System.getProperty("id", "")

        val httpClient = httpClient()

        runBlocking {
            try {
                val url = url(hostPort, serviceId)
                val status = get(httpClient, url)
                println(status)
                println("Service $serviceId stopped")
            } catch (e: Exception) {
                println(e.message)
            }
        }

    }

}