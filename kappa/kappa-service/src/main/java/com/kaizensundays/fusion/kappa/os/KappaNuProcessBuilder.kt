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
    private var jdk = true

    override fun isWindows(props: Properties) = props.getProperty("os.name").startsWith("Windows")

    @JsonIgnore
    override fun isWindows() = isWindows(System.getProperties())

    override fun setCommand(command: List<String>) = apply { this.command = command }

    override fun setWorkingDir(workingDir: Path) = apply { this.workingDir = workingDir }

    override fun setEnvironment(environment: Map<String, String>) = apply { this.environment = environment }

    override fun setProcessListener(listener: NuProcessHandler) = apply { this.processHandler = listener }

    override fun setJdk(jdk: Boolean): OSProcessBuilder {
        this.jdk = jdk
        return this
    }

    override fun start(): KappaProcess {
        require(command.isNotEmpty())

        return if (jdk) {
            val builder = ProcessBuilder(command)
            builder.directory(workingDir.toFile())
            builder.environment().putAll(this.environment)
            KappaNuProcess(JDKProcessWrapper(builder.start()))
        } else {
            val builder = NuProcessBuilder(command)
            builder.setCwd(workingDir)
            builder.setProcessListener(processHandler)
            builder.environment().putAll(this.environment)
            KappaNuProcess(NuProcessWrapper(builder.start()))
        }
    }

}