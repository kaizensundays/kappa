package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.TimeUnit
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

    @Test
    fun startProcessWithJDK() {

        val command = if (builder.isWindows(System.getProperties()))
            listOf("cmd", "/c", "dir", "*.xml")
        else
            listOf("ls", "*.xml")

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

        sleep(3_000)
    }

}