package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


/**
 * Created: Sunday 8/28/2022, 12:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class NuProcessBuilderImplTest : OsTestSupport() {

    private val builder = NuProcessBuilderImpl()

    @Test
    fun isWindows() {

        val props = Properties()
        props["os.name"] = "Windows"

        assertTrue(builder.isWindows(props))

        props["os.name"] = "Linux"

        assertFalse(builder.isWindows(props))
    }

    @Test
    fun startProcess() {

        val command = command()

        val process = builder.setCommand(command)
            .startProcess()

        val exitCode = process.waitFor(10, TimeUnit.SECONDS)

        assertEquals(0, exitCode)

        sleep(3_000)
    }

}