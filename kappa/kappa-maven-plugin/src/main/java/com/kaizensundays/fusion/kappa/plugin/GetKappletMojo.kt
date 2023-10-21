package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Kappa
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

        val conf = getConfiguration()
        println("$conf\n")

        val hostPort = conf.hosts.first()
        val host = hostPort.host
        val port = hostPort.port

        val httpClient = httpClient()

        runBlocking {
            try {
                val kapplet = getKapplet(httpClient, "http://$host:$port", 3)
                println("$kapplet\n")
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