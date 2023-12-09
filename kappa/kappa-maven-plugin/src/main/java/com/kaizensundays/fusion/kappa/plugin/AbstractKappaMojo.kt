package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.service.Configuration
import com.kaizensundays.fusion.kappa.Kappa
import com.kaizensundays.fusion.kappa.Kappa.MOJO_CONFIGURATION
import com.kaizensundays.fusion.kappa.service.Service
import com.kaizensundays.fusion.kappa.event.Event
import com.kaizensundays.fusion.kappa.event.JacksonObjectConverter
import com.kaizensundays.fusion.kappa.getPropertyAsLong
import com.kaizensundays.fusion.messaging.Instance
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
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.repository.RepositorySystem
import org.apache.maven.shared.transfer.artifact.resolve.ArtifactResolver
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

    val jsonConverter = JacksonObjectConverter<Event>(true)

    protected val nodeClient: NodeClient = DefaultNodeClient()

    protected var artifactManager: ArtifactManager = NopArtifactManager()

    private fun onMojo() {
        artifactManager = DefaultArtifactManager(
            pluginContext,
            session,
            artifactResolver,
            artifactHandlerManager,
            repositorySystem,
            pomRemoteRepositories
        )
    }

    fun init() {
        context = AnnotationConfigApplicationContext(KappaPluginContext::class.java)
        context.registerShutdownHook()
    }

    fun sleep(sec: Long = Kappa.MOJO_SLEEP_AFTER_SEC) {
        var delaySec = getPropertyAsLong(Kappa.MOJO_SLEEP_AFTER_PROP, sec)
        while (delaySec-- > 0) {
            print("${delaySec + 1} \r")
            System.out.flush()
            Thread.sleep(1 * 1000)
        }
    }

    fun loadProperties(location: String): Properties {
        val props = Properties()
        props.load(this.javaClass.classLoader.getResourceAsStream(location))
        return props
    }

    fun ConfigurableApplicationContext.getPropertyAsInt(name: String): Int {
        val value = this.environment.getProperty(name)
        return Integer.parseInt(value)
    }

    fun getConfiguration(location: String, env: MutableMap<String, String>): Configuration {

        val conf = Configuration()

        var hosts = env[Kappa.ENV_KAPPA_HOSTS]
        if (hosts == null) {
            val props = loadProperties(location)
            hosts = props.getProperty(Kappa.PROP_KAPPA_DEFAULT_HOSTS)
        }

        val hx = if (!hosts.isNullOrBlank()) hosts.split(',') else emptyList()

        conf.hosts = hx.map { it.split(':') }.map { Instance(it[0], Integer.parseInt(it[1])) }

        return conf
    }

    fun getConfiguration(): Configuration {
        return getConfiguration(MOJO_CONFIGURATION, System.getenv())
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

    suspend fun getKapplet(client: HttpClient, url: String = "", retries: Int): Service {
        val response: HttpResponse = client.get("$url/get-kapplet") {
            retry {
                maxRetries = retries
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

    fun checkKapplet(instance: Instance, retries: Int = 3): Boolean {

        val httpClient = httpClient()

        return runBlocking {
            try {
                val kapplet = getKapplet(httpClient, "http://${instance.host}:${instance.port}", retries)
                println("kapplet=$kapplet")
                true
            } catch (e: Exception) {
                println(e.message)
                false
            }
        }
    }

    fun kappletIsNotRunning(instance: Instance) = !checkKapplet(instance, 3)

    fun readText(location: String): String {
        return javaClass.getResource(location)?.readText() ?: "?"
    }

    abstract fun doExecute()

    override fun execute() {
        val t0 = System.currentTimeMillis()
        println()

        try {
            onMojo()
            doExecute()
        } catch (e: Exception) {
            println(e.message)
            e.printStackTrace()
        }

        println()
        val t1 = System.currentTimeMillis()
        println("Done ${t1 - t0} ms")
    }

    // ArtifactManager

    @Parameter(defaultValue = "\${session}", required = true, readonly = true)
    lateinit var session: MavenSession

    @Component
    lateinit var artifactResolver: ArtifactResolver

    @Component
    lateinit var artifactHandlerManager: ArtifactHandlerManager

    @Component
    lateinit var repositorySystem: RepositorySystem

    @Parameter(defaultValue = "\${project.remoteArtifactRepositories}", readonly = true, required = true)
    var pomRemoteRepositories: List<ArtifactRepository> = emptyList()

}