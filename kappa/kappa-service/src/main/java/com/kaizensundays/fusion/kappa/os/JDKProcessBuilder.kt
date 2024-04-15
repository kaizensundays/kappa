package com.kaizensundays.fusion.kappa.os

import com.fasterxml.jackson.annotation.JsonIgnore
import com.zaxxer.nuprocess.NuProcessHandler
import java.io.File
import java.nio.file.Path
import java.util.*

/**
 * Created: Saturday 12/9/2023, 6:16 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JDKProcessBuilder : OSProcessBuilder {

    var command: List<String> = emptyList()
    var workingDir = File(".").toPath()
    var environment: Map<String, String> = emptyMap()
    private var processHandler: NuProcessHandler = NoConsoleProcessHandler()
    private val console = JDKProcessConsole()

    override fun isWindows(props: Properties) = props.getProperty("os.name").startsWith("Windows")

    @JsonIgnore
    override fun isWindows() = isWindows(System.getProperties())

    override fun setCommand(command: List<String>) = apply { this.command = command }

    override fun setWorkingDir(workingDir: Path) = apply { this.workingDir = workingDir }

    override fun setEnvironment(environment: Map<String, String>) = apply { this.environment = environment }

    override fun setProcessListener(listener: NuProcessHandler) = apply { this.processHandler = listener }

    override fun start(): KappaProcess {
        require(command.isNotEmpty())

        val builder = ProcessBuilder(command)
        builder.directory(workingDir.toFile())
        builder.environment().putAll(this.environment)
        val process = builder.start()
        console.onStart(process)
        return KappaNuProcess(JDKProcessWrapper(process))
    }
}