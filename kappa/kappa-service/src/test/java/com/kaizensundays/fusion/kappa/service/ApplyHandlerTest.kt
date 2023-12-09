package com.kaizensundays.fusion.kappa.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Sunday 7/2/2023, 7:42 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ApplyHandlerTest {

    private val handler = ApplyHandler(DefaultPendingResults())

    private val deployments = Deployments()

    @Test
    fun getArtifacts() {

        val serviceMap = deployments.readBlocking("/deployment.yaml")

        val artifacts = handler.getArtifacts(serviceMap)

        assertEquals(1, artifacts.size)
        assertTrue(artifacts.first().contains("fusion-mu"))
    }

}