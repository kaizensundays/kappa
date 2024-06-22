package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Created: Saturday 12/17/2022, 12:09 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class MockOsTest {

    private val os = MockOs()

    @Test
    fun nextPID() {

        assertEquals(30000, os.nextPID())

        os.add(MockProcess(10000, "one"))
        assertEquals(30000, os.nextPID())

        os.add(MockProcess(30100, "one"))
        assertEquals(30101, os.nextPID())
    }

    @Test
    fun findPID() {

        assertEquals(0, os.findPID("1234567"))

        os.add(MockProcess(os.nextPID(), "... 1234567 ..."))
        assertEquals(30000, os.findPID("1234567"))

        os.add(MockProcess(os.nextPID(), "... 7654321 ..."))
        assertEquals(30001, os.findPID("7654321"))
    }

}