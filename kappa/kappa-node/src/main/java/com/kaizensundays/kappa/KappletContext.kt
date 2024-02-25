package com.kaizensundays.kappa

import com.kaizensundays.fusion.kappa.KappletKtorServer
import com.kaizensundays.fusion.kappa.cache.FileSystemCacheConfiguration
import com.kaizensundays.fusion.kappa.cache.FileSystemCacheManager
import com.kaizensundays.fusion.kappa.cache.InMemoryCache
import com.kaizensundays.fusion.kappa.cast
import com.kaizensundays.fusion.kappa.event.Handler
import com.kaizensundays.fusion.kappa.event.Request
import com.kaizensundays.fusion.kappa.isWindows
import com.kaizensundays.fusion.kappa.messages.ArtifactResolution
import com.kaizensundays.fusion.kappa.messages.Ping
import com.kaizensundays.fusion.kappa.os.Linux
import com.kaizensundays.fusion.kappa.os.NuProcessBuilderImpl
import com.kaizensundays.fusion.kappa.os.Os
import com.kaizensundays.fusion.kappa.os.Windows
import com.kaizensundays.fusion.kappa.service.Apply
import com.kaizensundays.fusion.kappa.service.ApplyHandler
import com.kaizensundays.fusion.kappa.service.ArtifactResolutionHandler
import com.kaizensundays.fusion.kappa.service.DefaultPendingResults
import com.kaizensundays.fusion.kappa.service.Kapplet
import com.kaizensundays.fusion.kappa.service.KappletProperties
import com.kaizensundays.fusion.kappa.service.PendingResults
import com.kaizensundays.fusion.kappa.service.PingHandler
import com.kaizensundays.fusion.kappa.service.Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import javax.cache.Cache
import javax.cache.CacheManager

/**
 * Created: Saturday 9/10/2022, 1:17 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Configuration
@PropertySource("classpath:/application.properties")
open class KappletContext {

    @Value("\${kapplet.server.port}")
    var serverPort = 0

    @Bean
    open fun props(): KappletProperties {
        return KappletProperties()
    }

    @Bean
    open fun os(): Os {
        return if (isWindows()) Windows() else Linux()
    }

    @Bean
    open fun cacheManager(): CacheManager {
        return FileSystemCacheManager()
    }

    @Bean
    open fun serviceStore(cacheManager: CacheManager, props: KappletProperties): Cache<String, String> {
        val configuration = FileSystemCacheConfiguration<String, String>(props.cacheLocation)
        return cacheManager.createCache("services", configuration)
    }

    @Bean
    open fun artifactResolutionPendingResults(): PendingResults<ArtifactResolution> {
        return DefaultPendingResults()
    }

    private val serviceCache = InMemoryCache<String, Service>()

    @Bean
    open fun applyHandler(
        artifactResolutionPendingResults: PendingResults<ArtifactResolution>, serviceStore: Cache<String, String>
    ): ApplyHandler {
        return ApplyHandler(artifactResolutionPendingResults, NuProcessBuilderImpl(), serviceStore, serviceCache)
    }

    @Bean
    open fun artifactResolutionHandler(artifactResolutionPendingResults: PendingResults<ArtifactResolution>): ArtifactResolutionHandler {
        return ArtifactResolutionHandler(artifactResolutionPendingResults)
    }

    @Bean
    open fun handlers(
        applyHandler: ApplyHandler,
        artifactResolutionHandler: ArtifactResolutionHandler
    ): Map<Class<out Request<*>>, Handler<*, *>> {
        return mapOf<Class<out Request<*>>, Handler<*, *>>(
            Ping::class.java to PingHandler(),
            Apply::class.java to applyHandler,
            ArtifactResolution::class.java to artifactResolutionHandler,
        )
    }

    @Bean
    open fun kapplet(os: Os, serviceStore: Cache<String, String>, handlers: Map<Class<out Request<*>>, Handler<*, *>>): Kapplet {
        @Suppress("UNCHECKED_CAST")
        val kapplet = Kapplet(os, NuProcessBuilderImpl(), serviceStore, serviceCache, handlers.cast())
        kapplet.enabled = false
        return kapplet
    }

    @Bean
    open fun ktorServer(os: Os, kapplet: Kapplet) = KappletKtorServer(serverPort, os, kapplet)

}