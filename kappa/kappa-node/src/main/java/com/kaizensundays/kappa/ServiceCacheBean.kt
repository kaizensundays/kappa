package com.kaizensundays.kappa

import com.kaizensundays.fusion.kappa.core.api.Service
import io.atomix.jcache.AtomicCacheConfiguration
import io.atomix.jcache.AtomicCacheManager
import org.springframework.beans.factory.FactoryBean
import javax.cache.Cache

/**
 * Created: Sunday 5/5/2024, 1:21 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ServiceCacheBean(
    private val cacheName: String,
    private val configuration: AtomicCacheConfiguration<String, Service>,
    private val manager: AtomicCacheManager
) : FactoryBean<Cache<String, Service>> {

    override fun getObject(): Cache<String, Service> {
        return manager.getOrCreateCache<String, Service, AtomicCacheConfiguration<String, Service>>(cacheName, configuration)
    }

    override fun getObjectType(): Class<*> {
        return ServiceCache::class.java
    }

}