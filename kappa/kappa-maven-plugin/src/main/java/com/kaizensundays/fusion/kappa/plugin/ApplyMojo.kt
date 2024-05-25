package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.core.api.Apply
import com.kaizensundays.fusion.kappa.core.api.Service
import com.kaizensundays.fusion.kappa.service.Deployments
import com.kaizensundays.fusion.ktor.KtorProducer
import com.kaizensundays.fusion.messaging.DefaultLoadBalancer
import kotlinx.coroutines.runBlocking
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.net.URI
import java.time.Duration

/**
 * Created: Sunday 12/11/2022, 12:14 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "apply", defaultPhase = LifecyclePhase.NONE)
class ApplyMojo : AbstractKappaMojo() {

    private val deployments = Deployments()

    fun renderVersion(version: Version, serviceMap: Map<String, Service>) {
        serviceMap.forEach { (_, service) ->
            service.artifact = service.artifact?.replace("""%%version%%""", version.value)
        }
    }

    override fun doExecute() {

        if (!log.isInfoEnabled) {
            System.setProperty("maven.mojo.quiet.mode.enabled", "true")
        }

        val fileName = System.getProperty("file", "")

        val serviceMap = runBlocking { deployments.readAndValidateDeployment(fileName) }

        println("serviceMap=$serviceMap")

        val version = artifactManager.getProjectVersion()
        println("version=$version")

        renderVersion(version, serviceMap)
        println("serviceMap=$serviceMap")

        val request = Apply(fileName, emptyMap(), serviceMap)

        println(request)

        val conf = getConfiguration()
        println("$conf\n")

        val instance = conf.hosts.first()

        val producer = KtorProducer(DefaultLoadBalancer(listOf(instance)))

        val body = jsonConverter.fromObject(request)

        val response = producer.request(URI("post:/handle"), body.toByteArray())
            .blockLast(Duration.ofSeconds(30))

        println(if (response != null) String(response) else "?")
    }

}