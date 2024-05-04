package com.kaizensundays.fusion.kappa.core

import javax.cache.Cache

/**
 * Created: Saturday 5/4/2024, 12:57 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
fun <K, V> Cache<K, V>.toMap(): Map<K, V> {
    return this.associate { e -> e.key to e.value }
}
