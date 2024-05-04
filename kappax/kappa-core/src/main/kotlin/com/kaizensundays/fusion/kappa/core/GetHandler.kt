package com.kaizensundays.fusion.kappa.core

import com.kaizensundays.fusion.kappa.core.api.GetRequest
import com.kaizensundays.fusion.kappa.core.api.GetResponse
import com.kaizensundays.fusion.kappa.core.api.Handler
import com.kaizensundays.fusion.kappa.core.api.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.cache.Cache

/**
 * Created: Sunday 4/28/2024, 1:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
open class GetHandler(private val serviceCache: Cache<String, Service>) : Handler<GetRequest, GetResponse> {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    open fun toMap(serviceCache: Cache<String, Service>): Map<String, Service> = serviceCache.toMap()

    override fun handle(request: GetRequest): GetResponse {
        return try {
            val services = toMap(serviceCache)
            GetResponse(services)
        } catch (e: Exception) {
            logger.error("", e)
            val response = request.response()
            response.code = 1
            response.text = e.message ?: ""
            response
        }
    }

}