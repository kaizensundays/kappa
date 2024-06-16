package com.kaizensundays.fusion.kappa.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kaizensundays.fusion.kappa.core.api.Apply
import com.kaizensundays.fusion.kappa.core.api.Service
import com.kaizensundays.fusion.kappa.os.MockOs
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.cache.Cache
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Saturday 12/17/2022, 11:41 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [KappletIntegrationTestContext::class])
class KappletIntegrationTest {

    private val jackson = ObjectMapper(YAMLFactory()).registerKotlinModule()

    private val uuidRegex = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[0-9a-f]{4}-[0-9a-f]{12}$".toRegex()

    @Autowired
    lateinit var os: MockOs

    @Autowired
    lateinit var serviceStore: Cache<String, String>

    @Autowired
    lateinit var kapplet: Kapplet

    @Test
    fun startKapplet() {

        var serviceMap = runBlocking {
            kapplet.deployments.readAndValidateDeployment("/kapplet-lib.yaml")
        }

        serviceMap = runBlocking {
            kapplet.doApply(Apply(emptyMap(), serviceMap))
        }

        assertEquals(1, serviceMap.size)

        assertEquals("kapplet", serviceMap.values.first().name)

        assertEquals(1, os.getProcesses().size)

        val entry = serviceStore.toList().first()

        val serviceId = entry.key
        assertTrue(serviceId.matches(uuidRegex))

        val yaml = entry.value

        val svc = jackson.readValue(yaml, Service::class.java)
        assertEquals("kapplet", svc.name)

        assertEquals(os.findPID(serviceId), svc.pid)

        val services = runBlocking {
            kapplet.getServices()
        }

        assertEquals(1, services.size)
    }

}