package com.kaizensundays.fusion.kappa.core

import com.kaizensundays.fusion.kappa.core.api.GetRequest
import com.kaizensundays.fusion.kappa.core.api.Service
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

    private lateinit var handler: GetHandler

    @Test
    fun returnsOk() {

        val serviceMap = mapOf(
            "box1" to Service("box1"),
            "box3" to Service("box3"),
        )

        handler = object : GetHandler(serviceCache) {
            override fun toMap(serviceCache: Cache<String, Service>): Map<String, Service> {
                return serviceMap
            }
        }

        val res = handler.handle(GetRequest())
        assertEquals(0, res.code)
    }

}