package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.core.api.Apply
import com.kaizensundays.fusion.kappa.service.Kapplet
import com.kaizensundays.fusion.messaging.Instance
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * Created: Sunday 6/11/2023, 12:23 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class WindowsNodeInstaller(artifactManager: ArtifactManager, mojo: AbstractKappaMojo) : NodeInstaller(artifactManager, mojo) {

    override fun install() {

        val conf = mojo.getConfiguration()
        println("$conf\n")

        val hostPort = Instance("localhost", conf.hosts.first().port)

        if (mojo.checkKapplet(hostPort, 1)) {
            println("Node is running")
            return
        }

        val fileName = "kapplet.yaml"

        var serviceMap = runBlocking { deployments.readAndValidateDeployment(fileName) }
        println("serviceMap=$serviceMap")

        renderVersion(version, serviceMap)
        println("serviceMap=$serviceMap")

        val artifactMap = artifactManager.resolveArtifacts(serviceMap) { artifact ->
            artifactManager.resolveArtifact(artifact).artifact.file.canonicalPath
        }

        println("artifactMap=$artifactMap")

        val context = AnnotationConfigApplicationContext(KappaPluginContext::class.java)
        context.registerShutdownHook()

        val kapplet = context.getBean(Kapplet::class.java)

        serviceMap = runBlocking { kapplet.doApply(Apply(fileName, artifactMap), true) }

        println("*serviceMap=$serviceMap")

        waitForNodeToStart(hostPort)
    }

    override fun uninstall() {
        println(javaClass.canonicalName)
    }
}