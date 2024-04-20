package com.kaizensundays.fusion.kappa.os

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created: Sunday 4/14/2024, 7:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JDKProcessConsole : ProcessHandler() {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun onStart(process: Process) {
        Thread({
            val inputStream: InputStream = process.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                logger.info(line)
            }
        }, "CONSOLE").start()
    }

}