package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.service.Service
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResult

/**
 * Created: Tuesday 6/13/2023, 8:16 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface ArtifactManager {

    fun getProjectVersion(): Version

    fun resolveArtifact(artifact: String): ArtifactResult

    fun resolveArtifacts(serviceMap: Map<String, Service>, artifactResolver: (String) -> String): Map<String, String>

}