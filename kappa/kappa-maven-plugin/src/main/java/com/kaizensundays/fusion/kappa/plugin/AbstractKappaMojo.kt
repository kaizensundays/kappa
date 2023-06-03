package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Service
import com.kaizensundays.fusion.kappa.TypeRef
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.retry
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.apache.maven.plugin.AbstractMojo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.util.*

/**
 * Created: Friday 11/11/2022, 12:21 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class AbstractKappaMojo : AbstractMojo() {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    lateinit var context: ConfigurableApplicationContext

    fun init() {
        context = AnnotationConfigApplicationContext(KappaPluginContext::class.java)
        context.registerShutdownHook()
    }

    fun loadProperties(location: String): Properties {
        val props = Properties()
        props.load(this.javaClass.classLoader.getResourceAsStream(location))
        return props
    }

    fun Properties.getPropertyAsInt(name: String): Int {
        val value = this.getProperty(name)
        return Integer.parseInt(value)
    }

    fun ConfigurableApplicationContext.getPropertyAsInt(name: String): Int {
        val value = this.environment.getProperty(name)
        return Integer.parseInt(value)
    }

    fun httpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
            }
        }
    }

    suspend fun getKapplet(client: HttpClient, url: String = ""): Service {
        val response: HttpResponse = client.get("$url/get-kapplet") {
            retry {
                delayMillis { retry ->
                    println("Connecting $url ($retry)")
                    1000
                }
            }
        }
        if (response.status == HttpStatusCode.OK) {
            return response.body()
        }
        throw RuntimeException(response.status.toString())
    }

    fun checkKapplet(port: Int): Boolean {

        val httpClient = httpClient()

        return runBlocking {
            try {
                val kapplet = getKapplet(httpClient, "http://localhost:$port")
                println("kapplet=$kapplet")
                true
            } catch (e: Exception) {
                println(e.message)
                false
            }
        }
    }

    fun kappletIsNotRunning(port: Int) = !checkKapplet(port)

    fun readText(location: String): String {
        return javaClass.getResource(location)?.readText() ?: "?"
    }

    suspend inline fun <reified T> get(client: HttpClient, url: String, responseTypeRef: TypeRef<T>): T {
        val response: HttpResponse = client.get(url)
        if (response.status == HttpStatusCode.OK) {
            return response.body()
        }
        throw RuntimeException(response.status.toString())
    }

    abstract fun doExecute()

    override fun execute() {
        val t0 = System.currentTimeMillis()
        println()

        try {
            doExecute()
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
        }

        println()
        val t1 = System.currentTimeMillis()
        println("Done ${t1 - t0} ms")
    }

}