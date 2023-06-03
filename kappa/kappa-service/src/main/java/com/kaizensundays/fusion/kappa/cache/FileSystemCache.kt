package com.kaizensundays.fusion.kappa.cache

import com.kaizensundays.fusion.kappa.unsupportedOperation
import org.springframework.core.io.FileSystemResource
import java.io.File
import javax.cache.Cache

/**
 * Created: Saturday 10/15/2022, 1:05 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class FileSystemCache<K, V>(private val cacheName: String, private val location: String) : AbstractCache<K, V>() {

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

        val r = FileSystemResource("$location/$cacheName")

        if (!r.exists() || r.file.isFile) {
            throw IllegalArgumentException()
        }

        val dir = r.file.listFiles() ?: emptyArray()

        val iter = dir.iterator()

        return object : MutableIterator<Cache.Entry<K, V>> {
            override fun hasNext(): Boolean {
                return iter.hasNext()
            }

            @Suppress("UNCHECKED_CAST")
            override fun next(): Cache.Entry<K, V> {
                val key = iter.next().name as K
                val value = get(key)
                return Entry(key, value)
            }

            override fun remove() {
                unsupportedOperation()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun get(key: K): V {
        val r = FileSystemResource("$location/$cacheName/$key")
        return if (r.exists()) {
            String(r.inputStream.readBytes())
        } else {
            ""
        } as V
    }

    override fun put(key: K, value: V) {
        val r = FileSystemResource("$location/$cacheName/$key")
        if (!r.exists()) {
            File("$location/$cacheName/$key").createNewFile()
        }
        r.outputStream.write(value.toString().toByteArray())
    }

    override fun remove(key: K): Boolean {
        val r = FileSystemResource("$location/$cacheName/$key")
        if (r.exists()) {
            return r.file.delete()
        }
        return false
    }

}