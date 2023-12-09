package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.service.Deployments
import com.kaizensundays.fusion.kappa.service.Service
import com.kaizensundays.fusion.kappa.isWindows
import com.kaizensundays.fusion.kappa.os.Linux
import com.kaizensundays.fusion.messaging.Instance
import kotlinx.coroutines.runBlocking
import java.net.ConnectException

/**
 * Created: Sunday 6/11/2023, 12:21 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class NodeInstaller(
    protected val artifactManager: ArtifactManager,
    protected val mojo: AbstractKappaMojo
) {

    companion object {
        fun installer(artifactManager: ArtifactManager, mojo: AbstractKappaMojo, isWindows: Boolean): NodeInstaller {
            return if (isWindows) WindowsNodeInstaller(artifactManager, mojo) else LinuxNodeInstaller(artifactManager, mojo, Linux())
        }

        fun installer(artifactManager: ArtifactManager, mojo: AbstractKappaMojo): NodeInstaller {
            return installer(artifactManager, mojo, isWindows())
        }
    }

    protected val deployments = Deployments()

    protected var version: Version = Version("")

    fun version(version: Version) = apply { this.version = version }

    fun renderVersion(version: Version, serviceMap: Map<String, Service>) {
        serviceMap.forEach { (_, service) ->
            service.artifact = service.artifact?.replace("""%%version%%""", version.value)
        }
    }

    fun waitForNodeToStart(instance: Instance) {

        val httpClient = mojo.httpClient()

        runBlocking {
            try {
                val kapplet = mojo.getKapplet(httpClient, "http://${instance.host}:${instance.port}", 3)
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

    abstract fun install()
    abstract fun uninstall()

}