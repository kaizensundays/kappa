package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.ktor.KtorProducer
import com.kaizensundays.fusion.messaging.DefaultLoadBalancer
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.net.URI
import java.time.Duration

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

        val instance = conf.hosts.first()

        val producer = KtorProducer(DefaultLoadBalancer(listOf(instance)))

        val response = producer.request(URI("get:/get-kapplet"))
            .blockLast(Duration.ofSeconds(30))

        val json = if (response != null) String(response) else "?"

        println(json)
    }

}