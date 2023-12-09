package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
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

    fun command(): List<String> {
        return if (builder.isWindows(System.getProperties()))
            listOf("cmd", "/c", "dir", "*.xml")
        else
            listOf("/bin/bash", "-c", "ls *.xml")
    }

    @Test
    fun startProcessWithJDK() {

        val command = command()

        val process = ProcessBuilder()
            .command(command)
            .start()

        val inputStream: InputStream = process.inputStream
        val reader = BufferedReader(InputStreamReader(inputStream))

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            println(line)
        }

        //val exitCode: Int = process.waitFor()
        val started = process.waitFor(30, TimeUnit.SECONDS)
        assertTrue(started)
        assertEquals(0, process.exitValue())

        sleep(3_000)
    }

    @Test
    fun startWithJdk() {

        val command = command()

        val process = builder.setCommand(command)
            .setJdk(true)
            .start()

        val exitCode = process.waitFor(10, TimeUnit.SECONDS)

        assertEquals(0, exitCode)

        sleep(3_000)
    }

    @Test
    fun startWithNu() {

        val command = command()

        val process = builder.setCommand(command)
            .setJdk(false)
            .start()

        val exitCode = process.waitFor(10, TimeUnit.SECONDS)

        assertEquals(0, exitCode)

        sleep(3_000)
    }

}