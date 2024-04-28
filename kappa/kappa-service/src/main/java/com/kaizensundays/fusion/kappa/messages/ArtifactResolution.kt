package com.kaizensundays.fusion.kappa.messages

import com.kaizensundays.fusion.kappa.core.api.Request

/**
 * Created: Tuesday 7/4/2023, 1:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
data class ArtifactResolution(
    val requestId: String,
    val artifactMap: Map<String, String>
) : Request<ArtifactResolutionAck>() {

    override fun response() = ArtifactResolutionAck()

}