package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Apply
import com.kaizensundays.fusion.kappa.Deployments
import com.kaizensundays.fusion.kappa.Service
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

/**
 * Created: Sunday 12/11/2022, 12:14 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "apply", defaultPhase = LifecyclePhase.NONE)
class ApplyMojo : AbstractKappaArtifactResolvingMojo() {

    private val deployments = Deployments()


    private suspend fun apply(client: HttpClient, url: String, fileName: String, artifactMap: Map<String, String>): String {

        val request = Apply(fileName, artifactMap)

        val response: HttpResponse = client.post("$url/apply") {
            setBody(request)
            contentType(ContentType.Application.Json)
        }
        if (response.status == HttpStatusCode.OK) {
            return response.body()
        }
        throw RuntimeException(response.status.toString())
    }

    override fun doExecute() {

        val fileName = System.getProperty("file", "")

        val serviceMap = runBlocking { deployments.readAndValidateDeployment(fileName) }

        println(serviceMap)

        val artifactMap = resolveArtifacts(serviceMap) { artifact ->
            resolveArtifact(artifact).artifact.file.canonicalPath
        }

        println(artifactMap)

        val props = loadProperties("application.properties")
        val port = props.getPropertyAsInt("kapplet.server.port")

        val httpClient = httpClient()

        val res = runBlocking { apply(httpClient, "http://localhost:$port", fileName, artifactMap) }

        println(res)
    }


/*
    override fun execute() {
        try {
            doExecute()
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
        }
    }
*/

}