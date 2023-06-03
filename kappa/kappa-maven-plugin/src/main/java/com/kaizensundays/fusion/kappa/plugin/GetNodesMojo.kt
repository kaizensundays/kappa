package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Node
import com.kaizensundays.fusion.kappa.TypeRef
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

/**
 * Created: Saturday 5/20/2023, 1:47 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "get-nodes", defaultPhase = LifecyclePhase.NONE)
class GetNodesMojo : AbstractKappaMojo() {

    override fun doExecute() {

        val props = loadProperties("application.properties")
        val port = props.getPropertyAsInt("kapplet.server.port")

        val httpClient = httpClient()

        val converter = Json { prettyPrint = true }

        val result = runBlocking { get(httpClient, "http://localhost:$port/get-nodes", TypeRef<List<Node>>()) }

        val json = converter.encodeToString(ListSerializer(Node.serializer()), result)

        println(json)
    }

}