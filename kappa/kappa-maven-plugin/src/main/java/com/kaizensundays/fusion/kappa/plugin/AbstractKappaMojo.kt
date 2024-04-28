package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Kappa
import com.kaizensundays.fusion.kappa.Kappa.MOJO_CONFIGURATION
import com.kaizensundays.fusion.kappa.core.api.Event
import com.kaizensundays.fusion.kappa.getPropertyAsLong
import com.kaizensundays.fusion.kappa.messages.JacksonObjectConverter
import com.kaizensundays.fusion.kappa.service.Configuration
import com.kaizensundays.fusion.kappa.core.api.Service
import com.kaizensundays.fusion.ktor.KtorProducer
import com.kaizensundays.fusion.messaging.DefaultLoadBalancer
import com.kaizensundays.fusion.messaging.Instance
import kotlinx.coroutines.runBlocking
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
import java.net.URI
import java.time.Duration
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

    /*
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
    */

    fun getKapplet(instance: Instance, retries: Int): Service {

        val producer = KtorProducer(DefaultLoadBalancer(listOf(instance)))

        val response = producer.request(URI("get:/get-kapplet"))
            .blockLast(Duration.ofSeconds(30))

        val json = if (response != null) String(response) else throw RuntimeException()

        return jsonConverter.toObject(json, Service::class.java)
    }

    fun checkKapplet(instance: Instance, retries: Int = 3): Boolean {

        return runBlocking {
            try {
                val kapplet = getKapplet(instance, retries)
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