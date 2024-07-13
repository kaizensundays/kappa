package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

/**
 * Created: Sunday 9/25/2022, 1:06 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class WindowsRemoteTest {

    private val os = Windows()

    @Test
    fun findPID() {

        val pid = os.findPID("1234567")

        assertTrue(pid > 0)
    }


    @Test
    fun serviceTagRegex() {

        val serviceTagRegex = os.serviceTagRegex("""com.kaizensundays.kappa.""")

        listOf(
            """ java  -Dcom.kaizensundays.kappa.service.id=1234567 """,
            """ python  --com.kaizensundays.kappa.service.id=3456712 """,
            """ bash  --com.kaizensundays.kappa.service.id=5671234 """,
        ).forEach { cmd ->
            assertTrue(serviceTagRegex.toRegex().matches(cmd))
        }
    }

}