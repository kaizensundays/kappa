package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.core.api.Event
import com.kaizensundays.fusion.kappa.core.api.Request
import com.kaizensundays.fusion.kappa.core.api.Response
import com.kaizensundays.fusion.kappa.messages.JacksonObjectConverter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

/**
 * Created: Wednesday 6/14/2023, 7:44 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class DefaultNodeClient : NodeClient() {

    private val jsonConverter = JacksonObjectConverter<Event>(true)

    override suspend fun get(client: HttpClient, url: String): HttpResponse {
        val response: HttpResponse = client.get(url)
        if (response.status == HttpStatusCode.OK) {
            return response.body()
        }
        throw RuntimeException(response.status.toString())
    }

    override suspend fun post(client: HttpClient, url: String, request: Request<Response>): String {

        val body = jsonConverter.fromObject(request)

        val response: HttpResponse = client.post(url) {
            setBody(body)
            contentType(ContentType.Application.Json)
        }
        if (response.status == HttpStatusCode.OK) {
            return response.body()
        }
        throw RuntimeException(response.status.toString())
    }

}