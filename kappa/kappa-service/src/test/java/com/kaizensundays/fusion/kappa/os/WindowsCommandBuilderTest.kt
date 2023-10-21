package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.EasyBoxMain
import com.kaizensundays.fusion.kappa.Service
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Created: Sunday 6/4/2023, 1:19 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class WindowsCommandBuilderTest {

    @Test
    fun build() {

        val services = listOf(
            Service(
                "s1", artifact = "easybox:0.0:jar",
                jvmOptions = mutableListOf("-Da", "-Db", "-Dc"),
                mainClass = EasyBoxMain::class.qualifiedName ?: ""
            ),
            Service(
                "s3", artifact = "kappa:0.0:jar",
                jvmOptions = mutableListOf("-Dd", "-De", "-Df"),
                mainClass = "com.kaizensundays.kappa.KappletMain",
                detached = true
            ),
            Service(
                "s7",
                command = mutableListOf("kappa.jar", "-Dtag=731", "-Dg", "-Dh", "-Di"),
                detached = true
            )
        )

        val javaHome = arrayOf(
            """C:\opt\java\jdk8u362""",
            """C:\opt\java\jdk-11.0.9.1""",
            """C:\opt\java\jdk-11.0.19""",
        )

        val artifactMap = mapOf(
            "easybox:0.0:jar" to "easybox.jar",
            "kappa:0.0:jar" to "kappa.jar"
        )

        val expectedCommands = listOf(
            listOf(
                """C:\opt\java\jdk8u362\bin\java""", "-cp", "easybox.jar", "-Dtag=0", "-Da", "-Db", "-Dc",
                "-Dloader.main=com.kaizensundays.fusion.kappa.EasyBoxMain", "org.springframework.boot.loader.PropertiesLauncher"
            ),
            listOf(
                "cmd", "/C", """C:\opt\java\jdk-11.0.9.1\bin\java""", "-cp", "kappa.jar", "-Dtag=1", "-Dd", "-De", "-Df",
                "-Dloader.main=com.kaizensundays.kappa.KappletMain", "org.springframework.boot.loader.PropertiesLauncher"
            ),
            listOf(
                "cmd", "/C", """C:\opt\java\jdk-11.0.19\bin\java""", "-cp", "kappa.jar", "-Dtag=731", "-Dg", "-Dh", "-Di"
            )
        )

        expectedCommands.zip(services).forEachIndexed { idx, (command, service) ->
            val env = mapOf("JAVA_HOME" to javaHome[idx])
            assertEquals(
                command, CommandBuilder.command(service, env, true) {
                    this.artifactMap = artifactMap
                    this.serviceTagPrefix = "tag="
                    this.serviceId = idx.toString()
                }
            )
        }
    }

}