package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS.WINDOWS
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Sunday 10/30/2022, 12:31 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@EnabledOnOs(WINDOWS)
class WindowsTest {

    private val os = Windows()

    @Test
    fun getPID() {

        assertTrue(os.getPID() > 0)
    }

    @Test
    fun returnsZeroPIDIfNoProcessFound() {


        val serviceId = UUID.randomUUID().toString()

        val pid = os.findPID(serviceId)

        assertEquals(0, pid)
    }

}