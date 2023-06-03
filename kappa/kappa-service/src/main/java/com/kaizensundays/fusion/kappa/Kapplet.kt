package com.kaizensundays.fusion.kappa

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kaizensundays.fusion.kappa.os.KappaProcess
import com.kaizensundays.fusion.kappa.os.OSProcessBuilder
import com.kaizensundays.fusion.kappa.os.Os
import com.kaizensundays.fusion.kappa.os.ServiceConsole
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import java.io.File
import java.io.InputStream
import java.util.*
import javax.annotation.PostConstruct
import javax.cache.Cache
import kotlin.coroutines.coroutineContext

/**
 * Created: Sunday 11/20/2022, 12:29 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class Kapplet(
    private val os: Os,
    private val processBuilder: OSProcessBuilder,
    private val serviceCache: Cache<String, String>,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    val jackson = ObjectMapper(YAMLFactory()).registerKotlinModule()

    val deployments = Deployments()

    val serviceIdToServiceMap = mutableMapOf<String, Service>()

    var enabled = false

    fun getServices(): Map<String, Service> {

        logger.info("serviceIdToServiceMap=$serviceIdToServiceMap")

        return HashMap(serviceIdToServiceMap)
    }

    fun getKapplet(): Service {

        val pid = os.getPID()

        return Service("kapplet", pid = pid)
    }

    fun getNodes(): List<Node> {
        return listOf(Node(UUID.randomUUID().toString(), "127.0.0.1", 0))
    }

    fun findServiceId(id: String): String? {

        return if (serviceIdToServiceMap.containsKey(id)) id
        else serviceIdToServiceMap.entries
            .find { (_, service) -> service.name == id }?.key
    }

    private fun writeYaml(pb: OSProcessBuilder): String {
        return try {
            jackson.writeValueAsString(pb)
        } catch (e: Exception) {
            logger.error("", e)
            "?"
        }
    }

    private fun startProcess(service: Service, start: Boolean): KappaProcess {

        val workingDir = getWorkingDir(service)
        File(workingDir).mkdirs()

        val command = if (start) service.command else service.stopCommand

        processBuilder.setCommand(command)
        processBuilder.setWorkingDir(File(workingDir).toPath())
        processBuilder.setEnvironment(service.env)

        val consoleFile = workingDir + File.separator + "console.log"
        processBuilder.setProcessListener(ServiceConsole(consoleFile, "%m"))

        logger.info("Starting process: {}", writeYaml(processBuilder))

        return processBuilder.start()
    }

    fun stopService(id: String, force: Boolean = false) {

        val serviceId = findServiceId(id)
        if (serviceId == null) {
            logger.warn("Service '$id' not found")
            return
        }

        val service = serviceIdToServiceMap[serviceId]
        if (!force && service?.name == "kapplet") {
            logger.warn("Kapplet won't stop itself")
            return
        }

        logger.info("Stopping $service ...")
        if (service == null) {
            return
        }

        if (service.stopCommand.isNotEmpty()) {

            val process = startProcess(service, false)
            logger.info("pid=${process.pid()}")

        } else {
            val pid = os.findPID(serviceId)

            if (pid > 0) {
                val result = os.shutdown(pid)
                logger.info("result=$result")
            }
        }

        serviceIdToServiceMap.remove(serviceId)
        serviceCache.remove(serviceId)

    }

    fun getKappletService(): Map<String, Service> {

        return serviceIdToServiceMap
            .filter { (_, service) -> service.name == "kapplet" }
    }

    fun stopKapplet(): List<String> {

        val map = getKappletService()

        if (map.size > 1) {
            println("WARN: More then one kapplet running")
        }

        return map.keys.map { serviceId ->
            println("Stopping $serviceId")
            stopService(serviceId, true)
            serviceId
        }
    }

    @Deprecated("Use getResourceAsInputStream")
    fun findResource(location: String): Resource {
        try {
            var resource: Resource = FileSystemResource(location)
            if (!resource.file.exists()) {
                resource = ClassPathResource(location)
            }
            if (!resource.exists() || !resource.isReadable) {
                throw IllegalArgumentException()
            }
            return resource
        } catch (e: Exception) {
            throw IllegalArgumentException(e)
        }
    }

    private fun getResourceAsInputStream(location: String): InputStream {

        val file = File(location)

        return if (file.exists()) {
            file.inputStream()
        } else {
            val name = if (location.startsWith('/')) location else "/$location"
            this.javaClass.getResourceAsStream(name) ?: throw IllegalArgumentException("File not found: $location")
        }
    }

    fun generateServiceId(service: Service): String {
        val serviceId = UUID.randomUUID().toString()

        service.command.forEachIndexed { i, c ->
            if (c == "\${" + Kappa.serviceTagPrefix.dropLast(1) + "}") {
                service.command[i] = "-D${Kappa.serviceTagPrefix}$serviceId"
            }
        }

        return serviceId
    }

    fun buildCommand(serviceId: String, service: Service, artifacts: Map<String, String>, detached: Boolean) {

        val command = mutableListOf<String>()

        if (detached) {
            command.add("cmd")
            command.add("/C")
        }

        command.add("java")
        command.add("-cp")
        val jar = artifacts[service.artifact] ?: ""
        command.add(jar)

        command.add("-D${Kappa.serviceTagPrefix}${serviceId}")
        command.addAll(service.jvmOptions)

        command.add("-Dloader.main=${service.mainClass}")
        command.add("org.springframework.boot.loader.PropertiesLauncher")

        service.command = command
    }

    fun startService(serviceId: String, service: Service) {
        logger.info("startService() >")

        val process = startProcess(service, true)
        logger.info("pid=${process.pid()}")

        service.process = process
        service.pid = process.pid()

        println("PID=${service.process?.pid()}:${service.process?.isRunning()}")
        println("PID=${process.pid()}:${process.isRunning()}")

        serviceCache.put(serviceId, jackson.writeValueAsString(service))
        serviceIdToServiceMap[serviceId] = service

        logger.info("startService() <")
    }

    @Deprecated("")
    fun readDeployment1(resource: Resource): Flow<Map<String, Service>> {

        return flowOf(resource.inputStream)
            .map { inputStream -> deployments.readValue(inputStream, Deployment::class.java) }
            .map { deployment -> deployment.services.map { service -> service.name to service }.toMap() }
    }

    fun getArtifactType(artifact: String): String {

        val tokens = artifact.split(":")

        return if (tokens.size >= 4) {
            tokens[3]
        } else {
            "?"
        }
    }

    fun unpackBundle(service: Service, artifacts: Map<String, String>) {

        val path = artifacts[service.artifact]

        if (path != null) {

            val workingDir = getWorkingDir(service)
            File(workingDir).mkdirs()

            extractTarBz2(File(path), File(workingDir))
        }
    }

    fun deployArtifact(serviceId: String, service: Service, artifacts: Map<String, String>, detached: Boolean): String {

        println("$serviceId::$service")

        val type = getArtifactType(service.artifact ?: "")

        if (type == "jar") {
            buildCommand(serviceId, service, artifacts, detached)
        }
        if (type == "tar.bz2") {
            unpackBundle(service, artifacts)
        }

        println("::$service")

        startService(serviceId, service)

        return serviceId
    }

    fun defaultDeploy(serviceId: String, service: Service): String {

        startService(serviceId, service)

        logger.info("serviceIdToServiceMap=${serviceIdToServiceMap}")

        return serviceId
    }

    fun deploy(service: Service, artifacts: Map<String, String>, detached: Boolean): String {

        return try {
            val serviceId = generateServiceId(service)

            if (service.artifact != null) {
                deployArtifact(serviceId, service, artifacts, detached)
            } else {
                defaultDeploy(serviceId, service)
            }
        } catch (e: Exception) {
            logger.error(e.message, e)
            e.javaClass.canonicalName + ": " + e.message
        }
    }

    suspend fun doApply(apply: Apply, detached: Boolean = false): Map<String, Service> {
        println("'${coroutineContext[CoroutineName.Key]}' apply()")

        println(apply)

        val inputStream = getResourceAsInputStream(apply.fileName)

        val serviceMap = deployments.readDeployment(inputStream)

        println(serviceMap)

        serviceMap.values.forEach { service -> deploy(service, apply.artifacts, detached) }

        return serviceMap
    }

    suspend fun apply(apply: Apply): String {

        CoroutineScope(dispatcherIO + CoroutineName("apply")).launch {
            try {
                doApply(apply)
            } catch (e: Exception) {
                logger.error(e.message, e)
            }
        }

        return "Ok"
    }

    fun getWorkingDir(service: Service): String {

        var dir = service.workingDir
        if (dir != null) {
            return dir
        }

        val currDir = File(".").canonicalPath

        dir = currDir + File.separator + Kappa.baseDir + File.separator + Kappa.workDir + File.separator + service.name

        dir = if (service.nameSuffix != null) dir + '-' + service.nameSuffix else dir

        return dir
    }

    fun test(): String {

        val dir = File(".")

        println("dir.exists=${dir.exists()}")

        println("dir.canonicalPath=${dir.canonicalPath}")

        val workingDir = getWorkingDir(Service("fusion-mu", "7703"))

        return "Ok - $workingDir"
    }

    fun findPID(serviceId: String): Int {
        return os.findPID(serviceId)
    }

    fun findNotRunning(serviceMap: Map<String, Service>): Map<String, Service> {

        return serviceMap.filter { (serviceId, _) -> os.findPID(serviceId) == 0 }
            .filter { (_, service) -> "kapplet" != service.name }
    }

    fun loop() {

        val notRunning = findNotRunning(serviceIdToServiceMap)

        logger.info("*** {}", notRunning)

        notRunning.forEach { (serviceId, service) ->
            logger.info("*** Starting service {} {}", serviceId, service)
            startService(serviceId, service)
        }
    }

    fun load(): Map<String, String> {

        val map = serviceCache.map { entry -> entry.key to entry.value }.toMap()

        map.forEach { entry ->
            val serviceId = entry.key
            val service = jackson.readValue(entry.value, Service::class.java)
            serviceIdToServiceMap[serviceId] = service
            logger.info("serviceId={}\n {}", serviceId, service)
        }

        return map
    }

    @PostConstruct
    fun start() {

        if (!enabled) {
            logger.info("Started without loop")
            return
        }

        load()

        loop()

        logger.info("Started")
    }

}