package com.kaizensundays.fusion.kappa.plugin.core

import com.kaizensundays.fusion.kappa.core.api.GetRequest
import com.kaizensundays.fusion.kappa.plugin.AbstractKappaMojo
import com.kaizensundays.fusion.ktor.KtorProducer
import com.kaizensundays.fusion.messaging.DefaultLoadBalancer
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.net.URI
import java.time.Duration

/**
 * Created: Sunday 12/11/2022, 2:51 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "get", defaultPhase = LifecyclePhase.NONE)
class GetMojo : AbstractKappaMojo() {

    override fun doExecute() {

        val conf = getConfiguration()
        println("$conf\n")

        val instance = conf.hosts.first()

        val producer = KtorProducer(DefaultLoadBalancer(listOf(instance)))

        val body = jsonConverter.fromObject(GetRequest())

        val response = producer.request(URI("post:/handle"), body.toByteArray())
            .blockLast(Duration.ofSeconds(30))

        val json = if (response != null) String(response) else "?"

        println(json)
    }

}