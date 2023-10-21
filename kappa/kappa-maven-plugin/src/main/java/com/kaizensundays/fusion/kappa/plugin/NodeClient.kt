package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.TypeRef
import com.kaizensundays.fusion.kappa.event.Request
import com.kaizensundays.fusion.kappa.event.Response
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

/**
 * Created: Wednesday 6/14/2023, 7:43 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class NodeClient {

    abstract suspend fun get(client: HttpClient, url: String): HttpResponse

    abstract suspend fun post(client: HttpClient, url: String, request: Request<Response>): String

    suspend inline fun <reified T> get(client: HttpClient, url: String, @Suppress("UNUSED_PARAMETER") responseTypeRef: TypeRef<T>): T {
        return get(client, url).body()
    }

}