package com.kaizensundays.fusion.kappa

import com.kaizensundays.fusion.kappa.cache.FileSystemCacheConfiguration
import com.kaizensundays.fusion.kappa.cache.FileSystemCacheManager
import com.kaizensundays.fusion.kappa.service.KappletProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.cache.Cache
import javax.cache.CacheManager

/**
 * Created: Saturday 9/3/2022, 1:03 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Configuration
open class KappaContext {

    @Value("\${kappa.server.port}")
    var serverPort = 0

    @Bean
    open fun props(): KappletProperties {
        return KappletProperties()
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
    open fun ktorServer() = KtorServer(serverPort)

}