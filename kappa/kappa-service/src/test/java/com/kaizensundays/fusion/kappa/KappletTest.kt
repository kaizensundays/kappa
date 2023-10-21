package com.kaizensundays.fusion.kappa

import com.kaizensundays.fusion.kappa.cache.InMemoryCache
import com.kaizensundays.fusion.kappa.event.Handler
import com.kaizensundays.fusion.kappa.event.Request
import com.kaizensundays.fusion.kappa.event.Response
import com.kaizensundays.fusion.kappa.messages.Ping
import com.kaizensundays.fusion.kappa.messages.PingResponse
import com.kaizensundays.fusion.kappa.os.KappaProcess
import com.kaizensundays.fusion.kappa.os.OSProcessBuilder
import com.kaizensundays.fusion.kappa.os.Os
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.core.io.ClassPathResource
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Created: Sunday 11/20/2022, 12:30 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KappletTest {

    private val cache = InMemoryCache<String, String>()

    private val os: Os = mock()

    private val pb: OSProcessBuilder = mock()

    private val handlers: MutableMap<Class<out Request<*>>, Handler<*, *>> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    private val kapplet = Kapplet(os, pb, cache, handlers as Map<Class<Request<Response>>, Handler<Request<Response>, Response>>)

    private val deployments = Deployments()

    private var easyboxYaml = ""

    private val artifacts = mapOf(
        "com.kaizensundays.particles:fusion-mu:0.0.0-SNAPSHOT:jar"
                to """C:\super\m2n\com\kaizensundays\particles\fusion-mu\0.0.0-SNAPSHOT\fusion-mu-0.0.0-SNAPSHOT.jar"""
    )

    @BeforeEach
    fun before() {
        val r = ClassPathResource("/easybox-1234567.yaml")
        easyboxYaml = String(r.inputStream.readBytes())
    }

    @Test
    fun serialize() {

        val kapplet = Service("kapplet", pid = 1)

        val json = Json.encodeToString(Service.serializer(), kapplet)

        assertEquals("""{"name":"kapplet","pid":1}""", json)
    }

    @Test
    fun getWorkingDir() {

        assertEquals(".", kapplet.getWorkingDir(Service("kappa", workingDir = ".")))

        assertTrue(kapplet.getWorkingDir(Service("fusion-alpha")).matches(""".*[/\\]fusion-alpha""".toRegex()))
        assertTrue(kapplet.getWorkingDir(Service("fusion-mu", "7701")).matches(""".*[/\\]fusion-mu-7701""".toRegex()))
        assertTrue(kapplet.getWorkingDir(Service("fusion-nu", "7703")).matches(""".*[/\\]fusion-nu-7703""".toRegex()))
    }

    @Test
    fun getServices() {

        var serviceMap = kapplet.getServices()

        assertEquals(0, serviceMap.size)

        val services = mapOf(
            "1" to Service("one", pid = 1),
            "3" to Service("one", pid = 3),
            "7" to Service("one", pid = 7),
        )

        kapplet.serviceIdToServiceMap.putAll(services)

        serviceMap = kapplet.getServices()

        assertEquals(3, serviceMap.size)
    }

    @Test
    fun buildCommand() {

        val serviceMap = deployments.readBlocking("deployment.yaml")

        val service = serviceMap["fusion-mu"]

        assertNotNull(service)

        val env = mapOf("JAVA_HOME" to "/opt/java/jdk11")

        kapplet.buildCommand("1234567", service, artifacts, env)

        if (isWindows()) {
            assertEquals(11, service.command.size)
            assertTrue(service.command[0].matches("""/opt/java/jdk11[/|\\]bin[/|\\]java""".toRegex()))
            assertEquals("-cp", service.command[1])
            assertEquals("-Dcom.kaizensundays.kappa.service.id.1234567", service.command[3])
            assertEquals("-Dloader.main=com.kaizensundays.particles.fusion.mu.Main", service.command[9])
            assertEquals("org.springframework.boot.loader.PropertiesLauncher", service.command[10])
        } else {
            assertEquals(3, service.command.size)
            assertTrue(service.command[2].matches("""/opt/java/jdk11[/|\\]bin[/|\\]java.*""".toRegex()))
        }
    }

    @Test
    fun generateServiceId() {

        val serviceMap = deployments.readBlocking("deployment.yaml")

        serviceMap.values.forEach { service ->

            val serviceId = kapplet.generateServiceId(service)

            if (service.artifact != null) {
                kapplet.buildCommand(serviceId, service, artifacts)
            }

            val prop = service.command.find { c -> c.contains("-D${Kappa.serviceTagPrefix}${serviceId}") }
            val env = service.env.values.find { v -> v.contains("-D${Kappa.serviceTagPrefix}") }

            assertTrue(prop != null || env != null, "Not found in $service")
        }
    }

    @Test
    fun getArtifactType() {

        assertEquals("?", kapplet.getArtifactType(""))
        assertEquals("jar", kapplet.getArtifactType("com.kaizensundays.particles:fusion-mu:0.0.0-SNAPSHOT:jar"))
        assertEquals("tar.bz2", kapplet.getArtifactType("com.kaizensundays.fusion.kappa.bundles:tomcat:9.0.73-SNAPSHOT:tar.bz2:bundle"))
    }

    @Test
    fun deployArtifact() {

        val serviceMap = deployments.readBlocking("deployment.yaml")

        val service = serviceMap["fusion-mu"]

        assertNotNull(service)

        val serviceId = "fusion-mu-uuid"

        val process: KappaProcess = mock()

        whenever(pb.start()).thenReturn(process)

        kapplet.deployArtifact(serviceId, service, emptyMap())
    }

    @Test
    fun apply() {

        val process: KappaProcess = mock()

        whenever(pb.start()).thenReturn(process)

        val apply = Apply("/deployment.yaml", emptyMap())

        val serviceMap = runBlocking { kapplet.doApply(apply) }

        assertEquals(4, serviceMap.size)

        verify(pb, Mockito.times(4)).start()
    }

    @Test
    fun stopServiceById() {

        val id1 = "1"
        val id2 = "2"
        val id3 = "3"

        cache.put(id1, "{}")
        cache.put(id3, "{}")

        kapplet.serviceIdToServiceMap[id1] = Service("service1")
        kapplet.serviceIdToServiceMap[id3] = Service("service3")

        whenever(os.findPID(id1)).thenReturn(10001)
        whenever(os.findPID(id2)).thenReturn(0)
        whenever(os.findPID(id3)).thenReturn(10003)

        kapplet.stopService(id1)
        kapplet.stopService(id2)
        kapplet.stopService(id3)

        verify(os).findPID(id1)
        verify(os).findPID(id3)

        verify(os).shutdown(10001)
        verify(os).shutdown(10003)

        verifyNoMoreInteractions(os)
    }

    @Test
    fun findNotRunning() {

        val service = kapplet.jackson.readValue(easyboxYaml, Service::class.java)

        val serviceMap = mapOf(
            "1" to service,
            "2" to service,
            "3" to service,
        )

        whenever(os.findPID("1")).thenReturn(10001)
        whenever(os.findPID("2")).thenReturn(0)
        whenever(os.findPID("3")).thenReturn(10003)

        val notRunning = kapplet.findNotRunning(serviceMap)

        assertEquals(1, notRunning.size)
    }

    @Test
    fun handle() {

        val pingHandler: PingHandler = mock()

        handlers[Ping::class.java] = pingHandler

        val ping = Ping()

        @Suppress("UNCHECKED_CAST")
        val wire = kapplet.jsonConverter.fromObject(ping as Request<Response>)

        whenever(pingHandler.handle(any())).thenReturn(PingResponse())

        val result = kapplet.handle(wire)
            .replace("\r", "")
            .replace("\n", "")

        assertTrue(result.matches(""".*""".toRegex()))

        verify(pingHandler).handle(any())
        verifyNoMoreInteractions(pingHandler)
    }

    @Test
    fun load() {

        var map = kapplet.load()
        assertTrue(map.isEmpty())

        val id1 = UUID.randomUUID().toString()

        cache.put(id1, easyboxYaml)

        map = kapplet.load()
        assertEquals(1, map.size)
        assertEquals(1, kapplet.serviceIdToServiceMap.size)
    }

}