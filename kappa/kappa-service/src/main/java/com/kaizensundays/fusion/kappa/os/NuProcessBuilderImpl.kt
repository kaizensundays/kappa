package com.kaizensundays.fusion.kappa.os

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.kaizensundays.fusion.kappa.os.api.KappaProcess
import com.kaizensundays.fusion.kappa.os.api.OSProcessBuilder
import com.zaxxer.nuprocess.NuProcessBuilder
import java.io.File
import java.nio.file.Path
import java.util.*

/**
 * Created: Sunday 8/28/2022, 12:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Deprecated("Use 'JDKProcessBuilder'")
@JsonIgnoreProperties(ignoreUnknown = true)
class NuProcessBuilderImpl : OSProcessBuilder {

    var command: List<String> = emptyList()
    var workingDir = File(".").toPath()
    var environment: Map<String, String> = emptyMap()
    private var processHandler: ProcessHandler = NoConsoleProcessHandler()
    private var consoleFileName = ""
    private var consoleLoggingPattern = ""

    override fun isWindows(props: Properties) = props.getProperty("os.name").startsWith("Windows")

    @JsonIgnore
    override fun isWindows() = isWindows(System.getProperties())

    override fun setCommand(command: List<String>) = apply { this.command = command }

    override fun setWorkingDir(workingDir: Path) = apply { this.workingDir = workingDir }

    override fun setEnvironment(environment: Map<String, String>) = apply { this.environment = environment }

    override fun setProcessListener(listener: Any) = apply {
        require(listener is ProcessHandler)
        this.processHandler = listener
    }

    override fun setConsole(fileName: String, pattern: String): OSProcessBuilder {
        this.consoleFileName = fileName
        this.consoleLoggingPattern = pattern
        return this
    }

    override fun start(): KappaProcess {
        require(command.isNotEmpty())

        val builder = NuProcessBuilder(command)
        builder.setCwd(workingDir)
        if (consoleFileName.isNotEmpty()) {
            processHandler = ServiceConsole(consoleFileName, consoleLoggingPattern)
        }
        builder.setProcessListener(processHandler)
        builder.environment().putAll(this.environment)
        return NuProcessWrapper(builder.start())
    }

}