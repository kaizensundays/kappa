package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Apply
import com.kaizensundays.fusion.kappa.Deployments
import com.kaizensundays.fusion.kappa.Kapplet
import com.kaizensundays.fusion.kappa.Service
import kotlinx.coroutines.runBlocking
import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.lang.Thread.sleep

/**
 * Created: Sunday 5/28/2023, 12:37 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "install", defaultPhase = LifecyclePhase.NONE)
class InstallMojo : AbstractKappaArtifactResolvingMojo() {

    private val deployments = Deployments()

    private fun renderVersion(version: String, serviceMap: Map<String, Service>) {
        serviceMap.forEach { (_, service) ->
            service.artifact = service.artifact?.replace("""%%version%%""", version)
        }
    }

    override fun doExecute() {

        val fileName = "kapplet.yaml"

        val pluginDescriptor = this.pluginContext["pluginDescriptor"] as PluginDescriptor

        val version = pluginDescriptor.version
        println("version=$version")

        var serviceMap = runBlocking { deployments.readAndValidateDeployment(fileName) }
        println("serviceMap=$serviceMap")

        renderVersion(version, serviceMap)
        println("serviceMap=$serviceMap")

        val artifactMap = resolveArtifacts(serviceMap) { artifact ->
            resolveArtifact(artifact).artifact.file.canonicalPath
        }

        println("artifactMap=$artifactMap")

        context = AnnotationConfigApplicationContext(KappaPluginContext::class.java)
        context.registerShutdownHook()

        val kapplet = context.getBean(Kapplet::class.java)

        serviceMap = runBlocking { kapplet.doApply(Apply(fileName, artifactMap), true) }

        println("*serviceMap=$serviceMap")

        sleep(60000)
    }

}