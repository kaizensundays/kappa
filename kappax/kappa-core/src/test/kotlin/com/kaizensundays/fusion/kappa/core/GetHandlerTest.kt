package com.kaizensundays.fusion.kappa.core

import com.kaizensundays.fusion.kappa.core.api.GetRequest
import com.kaizensundays.fusion.kappa.core.api.Service
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import javax.cache.Cache
import kotlin.test.assertEquals

/**
 * Created: Sunday 4/28/2024, 1:35 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class GetHandlerTest {

    private val serviceCache: Cache<String, Service> = mock()

    private val handler: GetHandler = GetHandler(serviceCache)

    @Test
    fun returnsOk() {

        val serviceMap = mapOf(
            "box1" to Service("box1"),
            "box3" to Service("box3"),
        )

        mockkStatic("com.kaizensundays.fusion.kappa.core.KappaCoreKt")
        every { any<Cache<String, Service>>().toMap() } returns serviceMap

        val res = handler.handle(GetRequest())
        assertEquals(0, res.code)
        assertEquals("Ok", res.text)
        assertEquals(serviceMap.entries.size, res.services.size)
    }

    @Test
    fun returnsError() {

        mockkStatic("com.kaizensundays.fusion.kappa.core.KappaCoreKt")
        every { any<Cache<String, Service>>().toMap() } throws IllegalStateException("error message")

        val res = handler.handle(GetRequest())

        assertEquals(1, res.code)
        assertEquals("error message", res.text)
    }

}