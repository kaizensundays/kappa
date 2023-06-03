package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS.LINUX

import kotlin.test.assertTrue

/**
 * Created: Saturday 11/26/2022, 11:34 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@EnabledOnOs(LINUX)
class LinuxTest {

    private val os = Linux()

    @Test
    fun getPID() {

        assertTrue(os.getPID() > 0)
    }

}