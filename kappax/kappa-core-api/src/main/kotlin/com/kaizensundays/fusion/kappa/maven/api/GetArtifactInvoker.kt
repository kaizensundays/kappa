package com.kaizensundays.fusion.kappa.maven.api

/**
 * Created: Sunday 5/26/2024, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface GetArtifactInvoker {

    fun execute(requestId: String, artifact: String, commandPort: Int)

}