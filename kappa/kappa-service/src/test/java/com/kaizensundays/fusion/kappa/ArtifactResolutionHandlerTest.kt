package com.kaizensundays.fusion.kappa

import com.kaizensundays.fusion.kappa.messages.ArtifactResolution
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

/**
 * Created: Sunday 7/9/2023, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ArtifactResolutionHandlerTest {

    private val pendingResults = DefaultPendingResults<ArtifactResolution>()

    private val handler = ArtifactResolutionHandler(pendingResults)

    @Test
    fun test() {

        val resolutions = mapOf(
            "1" to mapOf("artifact1" to "file1"),
            "3" to mapOf("artifact1" to "file1", "artifact3" to "file3"),
            "7" to mapOf("artifact1" to "file1", "artifact3" to "file3", "artifact7" to "file7"),
        )

        resolutions.forEach { (requestId, artifactMap) ->

            pendingResults.get(requestId)

            val msg = ArtifactResolution(requestId, artifactMap)

            handler.doHandle(msg)

            val result = pendingResults.get(requestId)

            val resolution = result.get(10, TimeUnit.SECONDS)

            requireNotNull(resolution)

            assertEquals(artifactMap.size, resolution.artifactMap.size)
        }

    }

}