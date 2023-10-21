package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Kappa
import com.kaizensundays.fusion.kappa.Service
import com.kaizensundays.fusion.kappa.TypeRef
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

/**
 * Created: Sunday 12/11/2022, 2:51 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "get", defaultPhase = LifecyclePhase.NONE)
class GetMojo : AbstractKappaMojo() {

    override fun doExecute() {

        val conf = getConfiguration()
        println("$conf\n")

        val hostPort = conf.hosts.first()

        val httpClient = httpClient()

        val converter = Json { prettyPrint = true }

        val result = runBlocking { nodeClient.get(httpClient, "http://${hostPort.host}:${hostPort.port}/get", TypeRef<Map<String, Service>>()) }

        val json = converter.encodeToString(MapSerializer(String.serializer(), Service.serializer()), result)

        println(json)
    }

}