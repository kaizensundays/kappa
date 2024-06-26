package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.cache.FileSystemCacheConfiguration
import com.kaizensundays.fusion.kappa.cache.FileSystemCacheManager
import com.kaizensundays.fusion.kappa.cache.InMemoryCache
import com.kaizensundays.fusion.kappa.core.api.isWindows
import com.kaizensundays.fusion.kappa.os.Linux
import com.kaizensundays.fusion.kappa.os.NuProcessBuilderImpl
import com.kaizensundays.fusion.kappa.os.Os
import com.kaizensundays.fusion.kappa.os.Windows
import com.kaizensundays.fusion.kappa.service.Kapplet
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import javax.cache.Cache
import javax.cache.CacheManager

/**
 * Created: Friday 11/11/2022, 11:32 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Configuration
@PropertySource("classpath:/mojo.properties")
open class KappaPluginContext {

    @Value("\${kapplet.cacheLocation}")
    var cacheLocation = ""

    @Bean
    open fun os(): Os {
        return if (isWindows()) Windows() else Linux()
    }

    @Bean
    open fun cacheManager(): CacheManager {
        return FileSystemCacheManager()
    }

    @Bean
    open fun serviceStore(cacheManager: CacheManager): Cache<String, String> {
        val configuration = FileSystemCacheConfiguration<String, String>(cacheLocation)
        return cacheManager.createCache("services", configuration)
    }

    @Bean
    open fun service(os: Os, serviceStore: Cache<String, String>): Kapplet {
        val kapplet = Kapplet(os, NuProcessBuilderImpl(), serviceStore, InMemoryCache(), emptyMap())
        kapplet.enabled = false
        return kapplet
    }

}
