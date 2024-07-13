package com.kaizensundays.kappa

import com.kaizensundays.fusion.kappa.cache.FileSystemCacheConfiguration
import com.kaizensundays.fusion.kappa.cache.FileSystemCacheManager
import com.kaizensundays.fusion.kappa.service.KappletProperties
import org.springframework.beans.factory.FactoryBean
import javax.cache.Cache

/**
 * Created: Saturday 5/25/2024, 12:56 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ServiceStoreBean(
    private val cacheName: String,
    private val props: KappletProperties
) : FactoryBean<Cache<String, String>> {

    override fun getObject(): Cache<String, String> {
        val configuration = FileSystemCacheConfiguration<String, String>(props.cacheLocation)
        val manager = FileSystemCacheManager()
        return manager.createCache(cacheName, configuration)
    }

    override fun getObjectType(): Class<*> {
        return Cache::class.java
    }
}