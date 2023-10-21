package com.kaizensundays.fusion.kappa.plugin

import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

/**
 * Created: Wednesday 6/21/2023, 7:03 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "uninstall", defaultPhase = LifecyclePhase.NONE)
class UninstallMojo : AbstractKappaMojo() {
    override fun doExecute() {

        val version = artifactManager.getProjectVersion()
        println("version=$version")

        NodeInstaller.installer(artifactManager, this)
            .version(version)
            .uninstall()

    }

}