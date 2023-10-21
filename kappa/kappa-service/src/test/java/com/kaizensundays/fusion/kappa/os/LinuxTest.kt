package com.kaizensundays.fusion.kappa.os

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

}