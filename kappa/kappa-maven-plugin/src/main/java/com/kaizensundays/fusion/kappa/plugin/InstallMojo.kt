package com.kaizensundays.fusion.kappa.plugin

import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

/**
 * Created: Sunday 5/28/2023, 12:37 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "install", defaultPhase = LifecyclePhase.NONE)
class InstallMojo : AbstractKappaMojo() {

    override fun doExecute() {

        val version = artifactManager.getProjectVersion()
        println("version=$version")

        NodeInstaller.installer(artifactManager, this)
            .version(version)
            .install()

        sleep()
    }

}