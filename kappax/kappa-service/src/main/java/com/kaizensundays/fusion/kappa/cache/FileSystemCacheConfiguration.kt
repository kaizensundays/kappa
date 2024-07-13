package com.kaizensundays.fusion.kappa.cache

import javax.cache.configuration.MutableConfiguration

/**
 * Created: Sunday 10/16/2022, 8:43 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FileSystemCacheConfiguration<K, V>(val cacheLocation: String) : MutableConfiguration<K, V>()
