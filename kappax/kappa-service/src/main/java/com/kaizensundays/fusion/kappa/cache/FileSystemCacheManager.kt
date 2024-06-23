package com.kaizensundays.fusion.kappa.cache

import com.kaizensundays.fusion.kappa.core.api.unsupportedOperation
import java.io.File
import java.net.URI
import java.util.*
import javax.cache.Cache
import javax.cache.CacheManager
import javax.cache.configuration.Configuration
import javax.cache.spi.CachingProvider

/**
 * Created: Sunday 10/16/2022, 8:16 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FileSystemCacheManager : CacheManager {
    override fun close() {
        // to be implemented
    }

    override fun getCachingProvider(): CachingProvider {
        unsupportedOperation()
    }

    override fun getURI(): URI {
        unsupportedOperation()
    }

    override fun getClassLoader(): ClassLoader {
        unsupportedOperation()
    }

    override fun getProperties(): Properties {
        unsupportedOperation()
    }

    override fun <K : Any?, V : Any?, C : Configuration<K, V>?> createCache(cacheName: String, configuration: C): Cache<K, V> {
        if (configuration is FileSystemCacheConfiguration<*, *>) {
            File("${configuration.cacheLocation}/$cacheName/").mkdirs()
            return FileSystemCache(cacheName, configuration.cacheLocation)
        }
        throw IllegalArgumentException()
    }

    override fun <K : Any?, V : Any?> getCache(cacheName: String, keyType: Class<K>?, valueType: Class<V>?): Cache<K, V> {
        unsupportedOperation()
    }

    override fun <K : Any?, V : Any?> getCache(cacheName: String?): Cache<K, V> {
        unsupportedOperation()
    }

    override fun getCacheNames(): MutableIterable<String> {
        unsupportedOperation()
    }

    override fun destroyCache(cacheName: String?) {
        unsupportedOperation()
    }

    override fun enableManagement(cacheName: String?, enabled: Boolean) {
        unsupportedOperation()
    }

    override fun enableStatistics(cacheName: String?, enabled: Boolean) {
        unsupportedOperation()
    }

    override fun isClosed(): Boolean {
        unsupportedOperation()
    }

    override fun <T : Any?> unwrap(clazz: Class<T>?): T {
        unsupportedOperation()
    }
}