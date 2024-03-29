package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.ktor.KtorProducer
import com.kaizensundays.fusion.messaging.DefaultLoadBalancer
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.net.URI
import java.time.Duration

/**
 * Created: Saturday 5/20/2023, 1:47 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "get-nodes", defaultPhase = LifecyclePhase.NONE)
class GetNodesMojo : AbstractKappaMojo() {

    override fun doExecute() {

        val conf = getConfiguration()
        println("$conf\n")

        val instance = conf.hosts.first()

        val producer = KtorProducer(DefaultLoadBalancer(listOf(instance)))

        val response = producer.request(URI("get:/get-nodes"))
            .blockLast(Duration.ofSeconds(30))

        val json = if (response != null) String(response) else "?"

        println(json)

        sleep()
    }

}