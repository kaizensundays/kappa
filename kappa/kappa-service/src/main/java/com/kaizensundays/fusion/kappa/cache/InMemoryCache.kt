package com.kaizensundays.fusion.kappa.cache

import com.kaizensundays.fusion.kappa.unsupportedOperation
import java.util.concurrent.ConcurrentHashMap
import javax.cache.Cache

/**
 * Created: Sunday 10/23/2022, 12:22 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class InMemoryCache<K, V> : AbstractCache<K, V>() {

    private val map: MutableMap<K, V> = ConcurrentHashMap()

    @Suppress("UnnecessaryVariable")
    class Entry<K, V>(private val key: K, private val value: V) : Cache.Entry<K, V> {
        override fun getKey(): K {
            return key
        }

        override fun getValue(): V {
            return value
        }

        override fun <T : Any?> unwrap(clazz: Class<T>?): T {
            unsupportedOperation()
        }
    }

    override fun iterator(): MutableIterator<Cache.Entry<K, V>> {
        return object : MutableIterator<Cache.Entry<K, V>> {
            val iter = map.iterator()
            override fun hasNext(): Boolean {
                return iter.hasNext()
            }

            override fun next(): Cache.Entry<K, V> {
                val entry = iter.next()
                return Entry(entry.key, entry.value)
            }

            override fun remove() {
                iter.remove()
            }
        }
    }

    override fun put(key: K, value: V) {
        map[key] = value
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(key: K): V {
        return map[key] ?: "" as V
    }

    override fun remove(key: K): Boolean {
        return (map.remove(key) != null)
    }
}