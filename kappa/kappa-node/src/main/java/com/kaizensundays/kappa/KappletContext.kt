package com.kaizensundays.kappa

import com.kaizensundays.fusion.kappa.Kapplet
import com.kaizensundays.fusion.kappa.KappletKtorServer
import com.kaizensundays.fusion.kappa.KappletProperties
import com.kaizensundays.fusion.kappa.cache.FileSystemCacheConfiguration
import com.kaizensundays.fusion.kappa.cache.FileSystemCacheManager
import com.kaizensundays.fusion.kappa.isWindows
import com.kaizensundays.fusion.kappa.os.KappaNuProcessBuilder
import com.kaizensundays.fusion.kappa.os.Linux
import com.kaizensundays.fusion.kappa.os.Os
import com.kaizensundays.fusion.kappa.os.Windows
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
    open fun serviceCache(cacheManager: CacheManager, props: KappletProperties): Cache<String, String> {
        val configuration = FileSystemCacheConfiguration<String, String>(props.cacheLocation)
        return cacheManager.createCache("services", configuration)
    }

    @Bean
    open fun service(os: Os, serviceCache: Cache<String, String>): Kapplet {
        val kapplet = Kapplet(os, KappaNuProcessBuilder(), serviceCache)
        kapplet.enabled = false
        return kapplet
    }

    @Bean
    open fun ktorServer(os: Os, service: Kapplet) = KappletKtorServer(serverPort, os, service)

}