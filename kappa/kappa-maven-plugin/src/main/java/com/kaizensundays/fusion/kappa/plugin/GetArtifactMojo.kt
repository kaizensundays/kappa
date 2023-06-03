package com.kaizensundays.fusion.kappa.plugin

import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter

/**
 * Created: Saturday 12/31/2022, 11:32 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "get-artifact", defaultPhase = LifecyclePhase.NONE, requiresProject = false)
class GetArtifactMojo : AbstractKappaArtifactResolvingMojo() {

    @Parameter(property = "artifact")
    private var artifact = ""

    override fun doExecute() {

        artifact = "com.kaizensundays.particles:fusion-mu:0.0.0-SNAPSHOT:jar"

        val result = resolveArtifact(artifact)

        println("file=${result.artifact.file} exist=${result.artifact.file.exists()}")
    }

}