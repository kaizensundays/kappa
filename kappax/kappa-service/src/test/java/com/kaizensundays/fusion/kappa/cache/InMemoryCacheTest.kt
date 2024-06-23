package com.kaizensundays.fusion.kappa.cache

import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


/**
 * Created: Sunday 10/23/2022, 12:24 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class InMemoryCacheTest {

    private val cache = InMemoryCache<String, String>()

    @Test
    fun numbers() {

        cache.put("0", "zero")
        cache.put("1", "one")
        cache.put("3", "three")
        cache.put("7", "seven")

        assertEquals("zero", cache["0"])
        assertEquals("one", cache["1"])
        assertEquals("three", cache["3"])
        assertEquals("seven", cache["7"])
        assertEquals("", cache["9"])

        val list = cache.toList()

        assertEquals(4, list.size)
    }

    @Test
    fun iterator() {

        assertEquals(listOf(), cache.toList())

        cache.put("0", "zero")

        var map = cache.toList().map { e -> e.key to e.value }.toMap()

        assertEquals(mapOf("0" to "zero"), map)

        cache.put("1", "one")
        cache.put("3", "three")

        map = cache.toList().map { e -> e.key to e.value }.toMap(TreeMap())

        assertEquals(mapOf("0" to "zero", "1" to "one", "3" to "three"), map)

        cache.put("7", "seven")

        map = cache.toList().map { e -> e.key to e.value }.toMap(TreeMap())

        assertEquals(mapOf("0" to "zero", "1" to "one", "3" to "three", "7" to "seven"), map)
    }

    @Test
    fun deployments() {

        val r = ClassPathResource("/easybox.yaml")

        val yaml = String(r.inputStream.readBytes())

        val key = UUID.randomUUID().toString()

        cache.put(key, yaml)

        assertTrue(cache[key].contains("- name: easybox"))
    }

    @Test
    fun remove() {

        cache.put("0", "zero")
        cache.put("1", "one")
        cache.put("3", "three")
        cache.put("7", "seven")

        assertTrue(cache.remove("0"))
        assertFalse(cache.remove("0"))
        assertTrue(cache.remove("1"))
        assertFalse(cache.remove("1"))

        assertEquals(2, cache.iterator().asSequence().toList().size)

        assertTrue(cache.remove("3"))
        assertFalse(cache.remove("3"))
        assertTrue(cache.remove("7"))
        assertFalse(cache.remove("7"))

        assertEquals(0, cache.iterator().asSequence().toList().size)
    }

}