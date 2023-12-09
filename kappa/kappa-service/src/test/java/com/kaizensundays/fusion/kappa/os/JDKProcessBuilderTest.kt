package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

/**
 * Created: Saturday 12/9/2023, 6:17 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JDKProcessBuilderTest : OsTestSupport() {

    private val builder = JDKProcessBuilder()

    @Test
    fun startProcess() {

        val command = command()

        val process = builder.setCommand(command)
            .start()

        val exitCode = process.waitFor(10, TimeUnit.SECONDS)

        assertEquals(0, exitCode)

        Thread.sleep(3_000)
    }

}