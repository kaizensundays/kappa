package com.kaizensundays.fusion.kappa.core

import com.kaizensundays.fusion.kappa.core.api.ArtifactResolution
import com.kaizensundays.fusion.kappa.core.api.ArtifactResolutionAck
import com.kaizensundays.fusion.kappa.core.api.PendingResults
import com.kaizensundays.fusion.kappa.core.api.ResponseCode

/**
 * Created: Tuesday 7/4/2023, 1:17 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ArtifactResolutionHandler(
    private val artifactResolutionPendingResults: PendingResults<ArtifactResolution>
) : AbstractHandler<ArtifactResolution, ArtifactResolutionAck>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun doHandle(resolution: ArtifactResolution): ArtifactResolutionAck {
        logger.info("*resolution=$resolution")

        val result = artifactResolutionPendingResults.get(resolution.requestId)

        result.put(resolution)

        logger.info("*result=$result")

        return response(ResponseCode.Ok, resolution)
    }

}