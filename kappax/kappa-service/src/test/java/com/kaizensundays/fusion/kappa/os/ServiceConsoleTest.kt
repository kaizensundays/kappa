package com.kaizensundays.fusion.kappa.os

import org.junit.jupiter.api.Test
import java.io.File
import java.time.Instant
import kotlin.test.assertEquals

/**
 * Created: Sunday 3/26/2023, 2:44 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ServiceConsoleTest {

    @Test
    fun test() {

        val fileName = "target/" + this.javaClass.simpleName + ".log"

        val className = javaClass.simpleName
        val timestamp = Instant.now().toString()

        val context = ServiceConsole(fileName, "(%t) %-5level [%c{1}] %m%n")

        context.onStart(null)

        val logger = context.getLogger(javaClass)

        logger.trace("{} {}", 1, timestamp)
        logger.debug("{} {}", 3, timestamp)
        logger.info("{} {}", 7, timestamp)

        context.onExit(0)

        val lines = File(fileName).readLines()

        val expectedLines = listOf(
            "(Test worker) TRACE [$className] 1 $timestamp",
            "(Test worker) DEBUG [$className] 3 $timestamp",
            "(Test worker) INFO  [$className] 7 $timestamp"
        )

        assertEquals(expectedLines, lines)
    }
}