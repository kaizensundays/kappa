package com.kaizensundays.fusion.kappa.os

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kaizensundays.fusion.kappa.service.Deployment
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Created: Sunday 1/8/2023, 11:46 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ObjectConversionTest {

    private val jackson = ObjectMapper(YAMLFactory()).registerKotlinModule()

    private fun formatYamlLine(line: String): String {

        return when {
            line.matches("^ {2}[^\\s\"].*".toRegex()) -> line.substring(2)
            line.matches("^ {4}\\S.*".toRegex()) -> line.substring(2)
            line.matches("^ {6}[A-Za-z].*".toRegex()) -> line.substring(2)
            line.matches("^ {6}-.*".toRegex()) -> line.substring(4)
            else -> line
        }.replace("{ }", "{}")
    }

    private fun formatYaml(yaml: String): String {

        return yaml.lines().fold(mutableListOf<String>()) { lines, line ->
            lines.add(formatYamlLine(line))
            lines
        }.joinToString("\n")
    }

    private fun readText(location: String): String {
        return javaClass.getResource(location)?.readText() ?: "?"
    }

    @Test
    fun convertDeployment() {

        var deploymentYaml = readText("/deployment.yaml")

        deploymentYaml = formatYaml(deploymentYaml)

        val deployment = jackson.readValue(deploymentYaml, Deployment::class.java)

        assertEquals(4, deployment.services.size)

        val serviceMap = deployment.services.map { service -> service.name to service }.toMap()

        assertEquals("com.kaizensundays.particles:fusion-mu:0.0.0-SNAPSHOT:jar", serviceMap["fusion-mu"]?.artifact)

        val tomcat = serviceMap["tomcat"]

        assertNotNull(tomcat)
        assertEquals(listOf("cmd", "/C", "bin\\shutdown.bat"), tomcat.stopCommand)

        val yaml = jackson.writeValueAsString(deployment)

        assertEquals(deploymentYaml, yaml)
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    fun convertKappaNuProcessBuilder() {

        val builder = NuProcessBuilderImpl()

        builder.setCommand(listOf("A", "B", "C"))
        builder.setWorkingDir(File("./target").toPath())
        builder.setEnvironment(mapOf("1" to "A", "2" to "B", "3" to "C"))

        val yaml = jackson.writeValueAsString(builder)

        var expected = readText("/" + javaClass.simpleName + ".KappaNuProcessBuilder.yaml")
        expected = formatYaml(expected)

        assertEquals(expected, yaml)
    }

}