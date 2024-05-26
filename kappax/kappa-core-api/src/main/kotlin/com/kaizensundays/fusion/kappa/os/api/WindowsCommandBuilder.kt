package com.kaizensundays.fusion.kappa.os.api

import com.kaizensundays.fusion.kappa.core.api.Service

/**
 * Created: Sunday 6/4/2023, 1:09 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class WindowsCommandBuilder(
    private val service: Service,
    private val env: Map<String, String>
) : CommandBuilder() {

    override fun build(): List<String> {

        val javaHome = env["JAVA_HOME"]
        require(!javaHome.isNullOrBlank())

        val command = mutableListOf<String>()

        if (service.detached) {
            command.add("cmd")
            command.add("/C")
        }

        command.add("""$javaHome\bin\java""")
        command.add("-cp")

        val artifact = service.artifact
        val jar = jar(artifact)

        if (jar.isNotBlank()) {
            command.add(jar.trim())
        }

        if (artifact != null) {
            command.add("-D${serviceTagPrefix}${serviceId}")
            service.jvmOptions.forEach { opt -> command.add(opt) }
            command.add("-Dloader.main=${service.mainClass}")
            command.add("org.springframework.boot.loader.PropertiesLauncher")
        } else {
            service.command.forEach { opt -> command.add(opt) }
        }

        return command
    }

}