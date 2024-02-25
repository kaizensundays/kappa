package com.kaizensundays.fusion.kappa.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kaizensundays.fusion.kappa.Kappa
import com.kaizensundays.fusion.kappa.event.Event
import com.kaizensundays.fusion.kappa.event.Handler
import com.kaizensundays.fusion.kappa.event.JacksonObjectConverter
import com.kaizensundays.fusion.kappa.event.Request
import com.kaizensundays.fusion.kappa.event.Response
import com.kaizensundays.fusion.kappa.event.ResponseCode
import com.kaizensundays.fusion.kappa.extractTarBz2
import com.kaizensundays.fusion.kappa.getResourceAsInputStream
import com.kaizensundays.fusion.kappa.os.CommandBuilder
import com.kaizensundays.fusion.kappa.os.KappaProcess
import com.kaizensundays.fusion.kappa.os.OSProcessBuilder
import com.kaizensundays.fusion.kappa.os.Os
import com.kaizensundays.fusion.kappa.os.ServiceConsole
import com.kaizensundays.fusion.kappa.toMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
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
    private val serviceStore: Cache<String, String>,
    val serviceIdToServiceMap: Cache<String, Service>,
    private val handlers: Map<Class<Request<Response>>, Handler<Request<Response>, Response>>,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    val yamlConverter = ObjectMapper(YAMLFactory()).registerKotlinModule()

    val jsonConverter = JacksonObjectConverter<Event>()

    val deployments = Deployments()

    var enabled = false

/*
    fun <K, V> Cache<K, V>.toMap(): Map<K, V> {
        return this.associate { e -> e.key to e.value }
    }
*/

    fun getServices(): Map<String, Service> {

        logger.info("serviceIdToServiceMap=$serviceIdToServiceMap")

        return serviceIdToServiceMap.toMap()
    }

    fun getKapplet(): Service {

        val pid = os.getPID()

        return Service("kapplet", pid = pid)
    }

    fun getNodes(): List<Node> {
        return listOf(Node(UUID.randomUUID().toString(), "127.0.0.1", 0))
    }

    fun findServiceId(id: String): String? {

        return if (serviceIdToServiceMap.toMap().containsKey(id)) id
        else serviceIdToServiceMap.toMap().entries
            .find { (_, service) -> service.name == id }?.key
    }

    private fun writeYaml(pb: OSProcessBuilder): String {
        return try {
            yamlConverter.writeValueAsString(pb)
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
        serviceStore.remove(serviceId)

    }

    fun getKappletService(): Map<String, Service> {

        return serviceIdToServiceMap.toMap()
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

    fun generateServiceId(service: Service): String {
        val serviceId = UUID.randomUUID().toString()

        service.command.forEachIndexed { i, c ->
            if (c == "\${" + Kappa.serviceTagPrefix.dropLast(1) + "}") {
                service.command[i] = "-D${Kappa.serviceTagPrefix}$serviceId"
            }
        }

        return serviceId
    }

    fun buildCommand(serviceId: String, service: Service, artifacts: Map<String, String>, env: Map<String, String>) {

        val command = CommandBuilder.command(service, env) {
            this.artifactMap = artifacts
            this.serviceTagPrefix = Kappa.serviceTagPrefix
            this.serviceId = serviceId
        }

        service.command.addAll(command)
    }

    fun buildCommand(serviceId: String, service: Service, artifacts: Map<String, String>) {

        buildCommand(serviceId, service, artifacts, System.getenv())
    }

    fun startService(serviceId: String, service: Service) {
        logger.info("startService() >")

        val process = startProcess(service, true)
        logger.info("pid=${process.pid()}")

        service.process = process
        service.pid = process.pid()

        println("PID=${service.process?.pid()}:${service.process?.isRunning()}")
        println("PID=${process.pid()}:${process.isRunning()}")

        serviceStore.put(serviceId, yamlConverter.writeValueAsString(service))
        serviceIdToServiceMap.put(serviceId, service)

        logger.info("startService() <")
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

    fun deployArtifact(serviceId: String, service: Service, artifacts: Map<String, String>): String {

        println("$serviceId::$service")

        val type = getArtifactType(service.artifact ?: "")

        if (type == "jar") {
            buildCommand(serviceId, service, artifacts)
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

    fun deploy(service: Service, artifacts: Map<String, String>): String {

        return try {
            val serviceId = generateServiceId(service)

            if (service.artifact != null) {
                deployArtifact(serviceId, service, artifacts)
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

        serviceMap.values.forEach { service -> deploy(service, apply.artifacts) }

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

    private fun systemError(text: String?): Response {
        val response = Response()
        response.code = ResponseCode.SystemError.code
        response.text = text ?: ResponseCode.SystemError.text
        return response
    }

    fun doHandle(wire: String): String {
        logger.info("< $wire")

        val request = jsonConverter.toObject(wire)

        require(request is Request<Response>)

        logger.info("< $request")

        val handler = handlers[request.javaClass]
        val response = if (handler != null) {
            handler.handle(request)
        } else {
            logger.info("Unexpected request type: ${request.javaClass}")
            request.response(ResponseCode.UnexpectedRequestType)
        }
        return jsonConverter.fromObject(response)
    }

    fun handle(wire: String): String {
        return try {
            doHandle(wire)
        } catch (e: Exception) {
            logger.error(e.message, e)
            jsonConverter.fromObject(systemError(e.message))
        }
    }

    fun loop() {

        val notRunning = findNotRunning(serviceIdToServiceMap.toMap())

        logger.info("*** {}", notRunning)

        notRunning.forEach { (serviceId, service) ->
            logger.info("*** Starting service {} {}", serviceId, service)
            startService(serviceId, service)
        }
    }

    fun load(): Map<String, String> {

        val map = serviceStore.map { entry -> entry.key to entry.value }.toMap()

        map.forEach { entry ->
            val serviceId = entry.key
            val service = yamlConverter.readValue(entry.value, Service::class.java)
            serviceIdToServiceMap.put(serviceId, service)
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