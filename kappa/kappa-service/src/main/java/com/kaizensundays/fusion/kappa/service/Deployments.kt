package com.kaizensundays.fusion.kappa.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kaizensundays.fusion.kappa.core.api.Service
import com.kaizensundays.fusion.kappa.getResourceAsInputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.InputStream

/**
 * Created: Sunday 2/5/2023, 11:17 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class Deployments(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private val jackson = ObjectMapper(YAMLFactory()).registerKotlinModule()

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun <T> readValue(inputStream: InputStream, type: Class<T>): T {
        return withContext(ioDispatcher) {
            jackson.readValue(inputStream, type)
        }
    }

    fun writeValueAsString(obj: Any): String {
        return jackson.writeValueAsString(obj)
    }

    suspend fun readDeployment(inputStream: InputStream): Map<String, Service> {

        val deployment = readValue(inputStream, Deployment::class.java)

        return deployment.services.map { service -> service.name to service }.toMap()
    }

    fun validate(service: Service) {

        val artifact = service.artifact

        if (!artifact.isNullOrBlank()
            && service.command.isNotEmpty() && !artifact.endsWith("bundle")
        ) {
            val yaml = writeValueAsString(service)
            throw IllegalArgumentException("'command' should not be assigned:\n$yaml")
        }

    }

    fun validate(serviceMap: Map<String, Service>) {

        serviceMap.values.forEach { service -> validate(service) }
    }

    suspend fun readAndValidateDeployment(fileName: String): Map<String, Service> {

        val inputStream = getResourceAsInputStream(fileName)

        val serviceMap = readDeployment(inputStream)


        validate(serviceMap)

        return serviceMap
    }

    fun readBlocking(location: String) = runBlocking { readAndValidateDeployment(location) }

}