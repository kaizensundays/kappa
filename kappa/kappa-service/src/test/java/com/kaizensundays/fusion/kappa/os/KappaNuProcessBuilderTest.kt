package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created: Sunday 8/28/2022, 12:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KappaNuProcessBuilderTest {

    private val builder = KappaNuProcessBuilder()

    @Test
    fun isWindows() {

        val props = Properties()
        props["os.name"] = "Windows"

        assertTrue(builder.isWindows(props))

        props["os.name"] = "Linux"

        assertFalse(builder.isWindows(props))
    }

}