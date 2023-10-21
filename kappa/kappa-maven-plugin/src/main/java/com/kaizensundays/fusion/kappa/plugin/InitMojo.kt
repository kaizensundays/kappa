package com.kaizensundays.fusion.kappa.plugin

import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.io.File

/**
 * Created: Sunday 5/14/2023, 12:04 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "init", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
class InitMojo : AbstractKappaMojo() {

    override fun doExecute() {

        val version = artifactManager.getProjectVersion()
        println("version=$version")

        var pom = readText("/init-pom.xml")

        pom = pom.replace("""%%version%%""", version.value)

        val file = File("pom.xml")

        if (file.exists()) {
            println("File $file already exist")
        } else {
            file.writeText(pom)
        }

    }

}