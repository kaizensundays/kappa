package com.kaizensundays.fusion.kappa.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kaizensundays.fusion.kappa.Kappa
import com.kaizensundays.fusion.kappa.core.api.Apply
import com.kaizensundays.fusion.kappa.core.api.ApplyResponse
import com.kaizensundays.fusion.kappa.core.api.ResponseCode
import com.kaizensundays.fusion.kappa.core.api.Service
import com.kaizensundays.fusion.kappa.extractTarBz2
import com.kaizensundays.fusion.kappa.messages.ArtifactResolution
import com.kaizensundays.fusion.kappa.os.CommandBuilder
import com.kaizensundays.fusion.kappa.os.KappaProcess
import com.kaizensundays.fusion.kappa.os.OSProcessBuilder
import org.apache.maven.shared.invoker.DefaultInvocationRequest
import org.apache.maven.shared.invoker.DefaultInvoker
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.cache.Cache

/**
 * Created: Sunday 7/2/2023, 7:41 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ApplyHandler(
    private val artifactResolutionPendingResults: PendingResults<ArtifactResolution>,
    private val processBuilder: OSProcessBuilder,
    private val serviceStore: Cache<String, String>,
    private val serviceCache: Cache<String, Service>
) : AbstractHandler<Apply, ApplyResponse>() {

    private val yamlConverter = ObjectMapper(YAMLFactory()).registerKotlinModule()

    fun getArtifacts(serviceMap: Map<String, Service>): List<String> {
        return serviceMap.values.mapNotNull { service -> service.artifact }
    }

    private fun resolveArtifact(requestId: String, artifact: String) {
        logger.info("resolveArtifact >")

        val mavenHome = System.getenv()["M2_HOME"]
        System.setProperty("maven.home", mavenHome ?: "")

        val request = DefaultInvocationRequest()
        request.goals = listOf("com.kaizensundays.fusion:kappa-maven-plugin:0.0.0-SNAPSHOT:get-artifact");
        request.properties = mapOf("requestId" to requestId, "artifact" to artifact).toProperties()
        request.baseDirectory = File(".");
        request.timeoutInSeconds = 10

        val invoker = DefaultInvoker()
        val result = invoker.execute(request)

        logger.info("resolveArtifact < {}", result)
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
        processBuilder.setConsole(consoleFile, "%m")

        logger.info("Starting process: {}", writeYaml(processBuilder))

        return processBuilder.start()
    }

    fun startService(serviceId: String, service: Service) {
        logger.info("startService() >")

        val process = startProcess(service, true)
        logger.info("pid=${process.pid()}")

        service.process = process
        service.pid = process.pid()
        println("PID=${process.pid()}:${process.isRunning()}")
        println("PID=${process.pid()}:${process.isRunning()}")

        serviceStore.put(serviceId, yamlConverter.writeValueAsString(service))
        serviceCache.put(serviceId, service)

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

        logger.info("serviceCache=${serviceCache}")

        return serviceId
    }

    fun deploy(service: Service, artifactMap: Map<String, String>): String {

        return try {
            val serviceId = generateServiceId(service)
            logger.info("serviceId=$serviceId")

            if (service.artifact != null) {
                deployArtifact(serviceId, service, artifactMap)
            } else {
                defaultDeploy(serviceId, service)
            }
        } catch (e: Exception) {
            logger.error(e.message, e)
            e.javaClass.canonicalName + ": " + e.message
        }
    }

    fun deploy(request: Apply, resolution: ArtifactResolution) {

        println("request.serviceMap=${request.serviceMap}")
        println("resolution.artifactMap=${resolution.artifactMap}")

        request.serviceMap.values.forEach { service -> deploy(service, resolution.artifactMap) }
    }

    override fun doHandle(request: Apply): ApplyResponse {
        logger.info("< $request")

        val requestId = UUID.randomUUID().toString()
        val pendingResult = artifactResolutionPendingResults.get(requestId)

        val artifacts = getArtifacts(request.serviceMap)

        val artifact = artifacts.first()

        resolveArtifact(requestId, artifact)

        val resolution = pendingResult.get(60, TimeUnit.SECONDS)
        logger.info("resolution=$resolution")

        if (resolution == null) {
            return request.response(ResponseCode.Timeout)
        }

        deploy(request, resolution)

        return request.response(ResponseCode.Ok)
    }

}