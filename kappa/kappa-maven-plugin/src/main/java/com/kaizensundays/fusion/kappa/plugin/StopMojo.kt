package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.ktor.KtorProducer
import com.kaizensundays.fusion.messaging.DefaultLoadBalancer
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.net.URI
import java.time.Duration

/**
 * Created: Sunday 1/29/2023, 11:23 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "stop", defaultPhase = LifecyclePhase.NONE)
open class StopMojo : AbstractKappaMojo() {

    open fun urlPath(serviceId: String): String {
        require(serviceId.isNotBlank())
        return "/stop/$serviceId"
    }

    override fun doExecute() {

        val conf = getConfiguration()
        println("$conf\n")

        val instance = conf.hosts.first()

        if (kappletIsNotRunning(instance)) {
            println("Kapplet is not running")
            return
        }

        val serviceId = System.getProperty("id", "")
        require(serviceId.isNotBlank())

        val producer = KtorProducer(DefaultLoadBalancer(listOf(instance)))

        val url = "get:" + urlPath(serviceId)

        val response = producer.request(URI(url))
            .blockLast(Duration.ofSeconds(30))

        val json = if (response != null) String(response) else "?"

        println(json)
    }

}