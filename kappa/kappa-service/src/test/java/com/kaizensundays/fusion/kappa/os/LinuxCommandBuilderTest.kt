package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.EasyBoxMain
import com.kaizensundays.fusion.kappa.core.api.Service
import com.kaizensundays.fusion.kappa.os.api.CommandBuilder
import com.kaizensundays.fusion.kappa.os.api.CommandTarget
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Created: Sunday 6/4/2023, 1:12 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class LinuxCommandBuilderTest {

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

        val artifactMap = mapOf(
            "easybox:0.0:jar" to "easybox.jar",
            "kappa:0.0:jar" to "kappa.jar"
        )

        val expected = mapOf(
            CommandTarget.Default to listOf(
                listOf("/bin/bash", "-c", "/home/user/jdk11/bin/java -cp easybox.jar -Dtag=0 -Da -Db -Dc -Dloader.main=com.kaizensundays.fusion.kappa.EasyBoxMain org.springframework.boot.loader.PropertiesLauncher"),
                listOf("/bin/bash", "-c", "/home/user/jdk11/bin/java -cp kappa.jar -Dtag=1 -Dd -De -Df -Dloader.main=com.kaizensundays.kappa.KappletMain org.springframework.boot.loader.PropertiesLauncher &"),
                listOf("/bin/bash", "-c", "/home/user/jdk11/bin/java -cp kappa.jar -Dtag=731 -Dg -Dh -Di &")
            ),
            CommandTarget.SystemUnit to listOf(
                listOf("#!/bin/bash", "/home/user/jdk11/bin/java -cp easybox.jar -Dtag=0 -Da -Db -Dc -Dloader.main=com.kaizensundays.fusion.kappa.EasyBoxMain org.springframework.boot.loader.PropertiesLauncher > console-0.log 2>&1"),
                listOf("#!/bin/bash", "/home/user/jdk11/bin/java -cp kappa.jar -Dtag=1 -Dd -De -Df -Dloader.main=com.kaizensundays.kappa.KappletMain org.springframework.boot.loader.PropertiesLauncher > console-1.log 2>&1"),
                listOf("#!/bin/bash", "/home/user/jdk11/bin/java -cp kappa.jar -Dtag=731 -Dg -Dh -Di > console-2.log 2>&1")
            )
        )

        fun expected(target: CommandTarget) = expected[target] ?: throw IllegalArgumentException(target.toString())

        val env = mapOf("JAVA_HOME" to "/home/user/jdk11")

        expected(CommandTarget.Default).zip(services).forEachIndexed { idx, (command, service) ->
            assertEquals(
                command, CommandBuilder.command(service, env, false) {
                    this.artifactMap = artifactMap
                    this.serviceTagPrefix = "tag="
                    this.serviceId = idx.toString()
                }
            )
        }

        expected(CommandTarget.SystemUnit).zip(services).forEachIndexed { idx, (command, service) ->
            assertEquals(
                command, CommandBuilder.command(service, env, false) {
                    this.artifactMap = artifactMap
                    this.serviceTagPrefix = "tag="
                    this.serviceId = idx.toString()
                    this.commandTarget = CommandTarget.SystemUnit
                }
            )
        }

    }

}