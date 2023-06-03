package com.kaizensundays.fusion.kappa

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

/**
 * Created: Sunday 2/5/2023, 11:25 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class DeploymentsTest {

    private val deployments = Deployments()

    @Test
    fun readDeployment() {

        val locations = listOf("deployment.yaml", "jconsole.yaml", "easybox-lib.yaml")

        val expectedSizes = listOf(4, 1, 1)

        locations.zip(expectedSizes).forEach { (location, size) ->

            val inputStream = deployments.getResourceAsInputStream(location)

            val serviceMap = runBlocking { deployments.readDeployment(inputStream) }

            assertEquals(size, serviceMap.size)
        }

    }

    @Test
    fun readDeploymentThrowsException() {

        assertThrows<Exception> {
            val inputStream = deployments.getResourceAsInputStream("no-such.yaml")
            runBlocking { deployments.readDeployment(inputStream) }
        }
    }

    @Test
    fun validate() {

        val locations = listOf("deployment.yaml", "jconsole.yaml", "easybox-lib.yaml")

        locations.forEach { location ->

            val inputStream = deployments.getResourceAsInputStream(location)

            val serviceMap = runBlocking { deployments.readDeployment(inputStream) }

            serviceMap.values.forEach { service -> deployments.validate(service) }

            val fusionMu = serviceMap["fusion-mu"]
            if (fusionMu != null) {
                fusionMu.command = mutableListOf("")
                assertThrows<IllegalArgumentException> { deployments.validate(fusionMu) }
            }
        }

    }

}