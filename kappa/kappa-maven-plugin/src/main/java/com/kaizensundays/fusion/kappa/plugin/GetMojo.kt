package com.kaizensundays.fusion.kappa.plugin

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

        val props = loadProperties("application.properties")
        val port = props.getPropertyAsInt("kapplet.server.port")

        val httpClient = httpClient()

        val converter = Json { prettyPrint = true }

        val result = runBlocking { get(httpClient, "http://localhost:$port/get", TypeRef<Map<String, Service>>()) }

        val json = converter.encodeToString(MapSerializer(String.serializer(), Service.serializer()), result)

        println(json)
    }

}