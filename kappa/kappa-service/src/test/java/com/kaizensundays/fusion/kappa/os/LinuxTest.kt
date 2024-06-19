package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.service.Result
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS.LINUX
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Saturday 11/26/2022, 11:34 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@EnabledOnOs(LINUX)
class LinuxTest {

    private val timeoutSec = 10L

    private val os = Linux()

    @Test
    fun getPID() {

        assertTrue(os.getPID() > 0)
    }

    @Test
    fun execute() {

        var result = os.execute(listOf("ls"), timeoutSec)
        assertEquals(0, result.code)

        result = os.execute(listOf("cat", "pom.xml"), timeoutSec)
        assertEquals(0, result.code)

        val javaHome = System.getenv()["JAVA_HOME"]
        require(!javaHome.isNullOrBlank())

        result = os.execute(listOf("/bin/bash", "-c", "$javaHome/bin/java -version"), timeoutSec)

        assertEquals(0, result.code)
    }

    @Test
    fun parsePID() {

        assertEquals(137, os.parsePID(Result(0, "137\n")))
        assertEquals(-1, os.parsePID(Result(1, "")))
        assertEquals(-3, os.parsePID(Result(3, "")))
        assertEquals(-999, os.parsePID(Result(0, "")))
        assertEquals(-999, os.parsePID(Result(0, "abc")))
    }


    @Test
    fun findPID() {

        val pid = os.findPID("cron")
        println("pid=$pid")
        assertTrue(pid > 0)
    }

}