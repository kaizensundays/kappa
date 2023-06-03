package com.kaizensundays.fusion.kappa.plugin

import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResolver
import java.io.File

/**
 * Created: Sunday 5/14/2023, 12:04 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "init", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
class InitMojo : AbstractKappaMojo() {

    override fun doExecute() {

        val pluginDescriptor = pluginContext["pluginDescriptor"] as PluginDescriptor

        val version = pluginDescriptor.version
        println("version=$version")

        var pom = readText("/init-pom.xml")

        pom = pom.replace("""%%version%%""", version)

        val file = File("pom.xml")

        if (file.exists()) {
            println("File $file already exist")
        } else {
            file.writeText(pom)
        }

    }

}