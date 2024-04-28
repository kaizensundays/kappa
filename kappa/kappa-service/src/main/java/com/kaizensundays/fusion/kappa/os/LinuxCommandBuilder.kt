package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.core.api.Service

/**
 * Created: Sunday 6/4/2023, 1:08 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class LinuxCommandBuilder(
    private val service: Service?,
    private val env: Map<String, String>
) : CommandBuilder() {

    constructor() : this(null, System.getenv())

    override fun build(command: String) = listOf("/bin/bash", "-c", command)

    override fun build(): List<String> {
        requireNotNull(service)

        val javaHome = env["JAVA_HOME"]
        require(!javaHome.isNullOrBlank())

        val artifact = service.artifact

        val jar = jar(artifact)

        val jvmOpts = if (artifact != null) {
            var jvmOpts = "-D${serviceTagPrefix}${serviceId} "
            jvmOpts += service.jvmOptions.joinToString(separator = " ")
            jvmOpts += " -Dloader.main=${service.mainClass} org.springframework.boot.loader.PropertiesLauncher"
            jvmOpts
        } else {
            service.command.joinToString(separator = " ")
        }

        return when (commandTarget) {
            CommandTarget.Default -> {
                val detach = if (service.detached) " &" else ""
                build("$javaHome/bin/java -cp $jar$jvmOpts$detach")
            }

            CommandTarget.SystemUnit ->
                listOf("#!/bin/bash", "$javaHome/bin/java -cp $jar$jvmOpts > console-$serviceId.log 2>&1")
        }
    }

}