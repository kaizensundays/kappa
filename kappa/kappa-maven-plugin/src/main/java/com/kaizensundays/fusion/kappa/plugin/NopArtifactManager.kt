package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.core.api.Service
import com.kaizensundays.fusion.kappa.unsupportedOperation
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResult

/**
 * Created: Tuesday 6/13/2023, 8:16 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class NopArtifactManager : ArtifactManager {

    override fun getProjectVersion(): Version = unsupportedOperation()

    override fun resolveArtifact(artifact: String): ArtifactResult = unsupportedOperation()

    override fun resolveArtifacts(serviceMap: Map<String, Service>, artifactResolver: (String) -> String): Map<String, String> = unsupportedOperation()

}