package com.kaizensundays.fusion.kappa.os

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.zaxxer.nuprocess.NuProcessBuilder
import com.zaxxer.nuprocess.NuProcessHandler
import java.io.File
import java.nio.file.Path
import java.util.*

/**
 * Created: Sunday 8/28/2022, 12:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class KappaNuProcessBuilder : OSProcessBuilder {

    var command: List<String> = emptyList()
    var workingDir = File(".").toPath()
    var environment: Map<String, String> = emptyMap()
    private var processHandler: NuProcessHandler = NoConsoleProcessHandler()

    override fun isWindows(props: Properties) = props.getProperty("os.name").startsWith("Windows")

    @JsonIgnore
    override fun isWindows() = isWindows(System.getProperties())

    override fun setCommand(command: List<String>): OSProcessBuilder {
        this.command = command
        return this
    }

    override fun setWorkingDir(workingDir: Path): OSProcessBuilder {
        this.workingDir = workingDir
        return this
    }

    override fun setEnvironment(environment: Map<String, String>): OSProcessBuilder {
        this.environment = environment
        return this
    }

    override fun setProcessListener(listener: NuProcessHandler): OSProcessBuilder {
        this.processHandler = listener
        return this
    }

    override fun start(): KappaProcess {
        if (command.isEmpty()) {
            throw IllegalArgumentException()
        }
        val builder = NuProcessBuilder(command)
        builder.setCwd(workingDir)
        builder.setProcessListener(processHandler)
        builder.environment().putAll(this.environment)
        return KappaNuProcess(builder.start(), this)
    }

}