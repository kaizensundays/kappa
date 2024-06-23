package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.core.api.Kappa
import com.kaizensundays.fusion.kappa.service.Result
import com.kaizensundays.fusion.kappa.os.api.CommandBuilder
import com.kaizensundays.fusion.kappa.os.api.CommandTarget
import com.kaizensundays.fusion.kappa.os.Linux
import com.kaizensundays.fusion.kappa.os.api.LinuxCommandBuilder
import com.kaizensundays.fusion.kappa.core.api.uppercaseFirstChar
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

/**
 * Created: Sunday 6/11/2023, 12:23 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class LinuxNodeInstaller(artifactManager: ArtifactManager, mojo: AbstractKappaMojo, private val os: Linux) :
    NodeInstaller(artifactManager, mojo) {

    val HR = "-".repeat(32)

    var env: Map<String, String> = System.getenv()

    var nodeName = Kappa.NODE_SERVICE_NAME

    fun systemdUnitFileLocation(home: String) = home.removeSuffix("/") + "/.config/systemd/user/"

    private fun systemdUnitFilePath(nodeName: String, home: String) = systemdUnitFileLocation(home) + "$nodeName.service"

    private fun execStartFilePath(nodeName: String, home: String) = systemdUnitFileLocation(home) + "$nodeName.sh"

    fun createSystemdUnitFile(name: String, location: String, desc: String, execStart: String, workingDir: String) {

        File(location).mkdirs()
        val file = File(location + name)

        val content = """
            [Unit]
            Description=$desc
            After=network.target

            [Service]
            ExecStart=$execStart
            WorkingDirectory=$workingDir
            Restart=always
            RestartSec=10

            [Install]
            WantedBy=default.target
        """.trimIndent()

        println("Creating unit file '${file.canonicalPath}':")
        println(HR)
        println(content)
        println(HR)

        file.writeText(content)
    }

    fun createExecStartFile(name: String, location: String, command: List<String>) {

        File(location).mkdirs()
        val file = File(location + name)

        val content = command.joinToString("\n")

        println("Creating execStart file '${file.canonicalPath}':")
        println(HR)
        println(content)
        println(HR)

        file.writeText(content)
        file.setExecutable(true)
    }

    fun execStartCommand(env: Map<String, String>): List<String> {

        val fileName = "kapplet.yaml"

        val serviceMap = runBlocking { deployments.readAndValidateDeployment(fileName) }
        println("serviceMap=$serviceMap")

        renderVersion(version, serviceMap)
        println("serviceMap=$serviceMap")

        val artifactMap = artifactManager.resolveArtifacts(serviceMap) { artifact ->
            artifactManager.resolveArtifact(artifact).artifact.file.canonicalPath
        }

        println("artifactMap=$artifactMap")

        val service = serviceMap["kapplet"]
        require(service != null)

        service.detached = false

        val serviceId = UUID.randomUUID().toString()

        val command = CommandBuilder.command(service, env) {
            this.artifactMap = artifactMap
            this.serviceTagPrefix = Kappa.serviceTagPrefix
            this.serviceId = serviceId
            this.commandTarget = CommandTarget.SystemUnit
        }

        return command
    }

    private fun systemCtlUser(cmd: String, unit: String): Result {
        return os.execute(LinuxCommandBuilder().build("systemctl --user $cmd $unit"), 10)
    }

    fun serviceStatus(name: String) = systemCtlUser("status", name)

    fun serviceEnable(name: String) = systemCtlUser("enable", name)

    fun serviceDisable(name: String) = systemCtlUser("disable", name)

    fun serviceStart(name: String) = systemCtlUser("start", name)

    fun serviceStop(name: String) = systemCtlUser("stop", name)

    override fun install() {

        val env = System.getenv()

        val home = env["HOME"]
        require(!home.isNullOrBlank())

        val node = Kappa.NODE_SERVICE_NAME

        val location = "$home/.config/systemd/user/"
        val execStart = "$location$node.sh"
        val workingDir = File(".").canonicalPath

        val command = execStartCommand(env)
        println("command=$command")

        println("Creating service '$node'")

        createExecStartFile("$node.sh", location, command)

        val desc = node.uppercaseFirstChar()

        createSystemdUnitFile("$node.service", location, desc, execStart, workingDir)

        var result = serviceStatus(node)
        println(result)
        if (result.code != 0) {
            println("Enabling service '$node'")
            result = serviceEnable(node)
            println(result)
            if (result.code == 0) {
                println("Starting service '$node'")
                result = serviceStart(node)
                println(result)
            }
        }

    }

    override fun uninstall() {
        val home = env["HOME"]
        require(!home.isNullOrBlank())

        println("Stopping $nodeName ...")

        var result = serviceStop(nodeName)
        println(result)
        if (result.code != 0) {
            println("Unable to stop $nodeName")
            return
        }

        result = serviceDisable(nodeName)
        println(result)
        if (result.code != 0) {
            println("Unable to deactivate $nodeName")
            return
        }

        deleteFile(systemdUnitFilePath(nodeName, home))
        deleteFile(execStartFilePath(nodeName, home))
    }

    private fun deleteFile(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
            if (file.exists()) {
                println("Unable to delete file $path")
            }
        }
    }

}