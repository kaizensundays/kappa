package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Saturday 12/9/2023, 6:17 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JDKProcessBuilderTest : OsTestSupport() {

    private val builder = JDKProcessBuilder()

    private val consoleFile = "target/" + System.currentTimeMillis() + ".console.log"
    private val consolePattern = "%m\n"

    @Test
    fun startProcessWithJDK() {

        val command = command()

        val process = ProcessBuilder()
            .command(command)
            .start()

        JDKProcessConsole(consoleFile, consolePattern).onStart(process)

        //val exitCode: Int = process.waitFor()
        val started = process.waitFor(30, TimeUnit.SECONDS)
        assertTrue(started)
        assertEquals(0, process.exitValue())

        Thread.sleep(1_000)
    }

    @Test
    fun startProcess() {

        val command = command()

        val process = builder.setCommand(command)
            .setConsole(consoleFile, consolePattern)
            .startProcess()

        val exitCode = process.waitFor(10, TimeUnit.SECONDS)

        assertEquals(0, exitCode)

        Thread.sleep(1_000)
    }

}