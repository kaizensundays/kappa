package com.kaizensundays.fusion.kappa.plugin

import kotlinx.coroutines.runBlocking
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.net.ConnectException

/**
 * Created: Sunday 11/27/2022, 11:36 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "get-kapplet", defaultPhase = LifecyclePhase.NONE)
class GetKappletMojo : AbstractKappaMojo() {

    override fun doExecute() {

        val props = loadProperties("application.properties")
        val port = props.getPropertyAsInt("kapplet.server.port")

        val httpClient = httpClient()

        runBlocking {
            try {
                val kapplet = getKapplet(httpClient, "http://localhost:$port")
                println("kapplet=$kapplet")
            } catch (e: ConnectException) {
                println(e.message)
                println()
                println("Kapplet is not running")
            } catch (e: Exception) {
                println(e.message)
                e.printStackTrace()
            }
        }
    }

}