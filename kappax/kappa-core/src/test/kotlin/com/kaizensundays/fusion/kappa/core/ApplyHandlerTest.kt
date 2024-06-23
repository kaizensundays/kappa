package com.kaizensundays.fusion.kappa.core

//import com.kaizensundays.fusion.kappa.cache.InMemoryCache
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Sunday 7/2/2023, 7:42 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ApplyHandlerTest {

    //private val handler = ApplyHandler(mock(), DefaultPendingResults(), mock(), mock(), InMemoryCache())
    private val handler = ApplyHandler(mock(), mock(), mock(), mock(), mock())

    private val deployments = Deployments()

    @Test
    fun getArtifacts() {

        val serviceMap = deployments.readBlocking("/deployment.yaml")

        val artifacts = handler.getArtifacts(serviceMap)

        assertEquals(1, artifacts.size)
        assertTrue(artifacts.first().contains("fusion-mu"))
    }

}