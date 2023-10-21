package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Kapplet
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
    open fun serviceCache(cacheManager: CacheManager): Cache<String, String> {
        val configuration = FileSystemCacheConfiguration<String, String>(cacheLocation)
        return cacheManager.createCache("services", configuration)
    }

    @Bean
    open fun service(os: Os, serviceCache: Cache<String, String>): Kapplet {
        val kapplet = Kapplet(os, KappaNuProcessBuilder(), serviceCache, emptyMap())
        kapplet.enabled = false
        return kapplet
    }

}
