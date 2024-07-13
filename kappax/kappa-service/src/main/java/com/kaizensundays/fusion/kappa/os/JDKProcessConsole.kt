package com.kaizensundays.fusion.kappa.os

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Created: Sunday 4/14/2024, 7:55 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JDKProcessConsole(private val fileName: String, private val pattern: String) : ProcessHandler() {

    lateinit var logger: org.apache.logging.log4j.Logger

    private val context = LoggerContext("ConsoleLoggerContext")

    override fun onStart(process: Process) {

        val builder = ConfigurationBuilderFactory.newConfigurationBuilder()
        builder.setStatusLevel(Level.INFO)
        builder.setConfigurationName("ConsoleLogger")

        val appenderBuilder = builder.newAppender("File", "File")
            .addAttribute("fileName", fileName)
            .addAttribute("immediateFlush", true)
            .addAttribute("append", false)
            .add(
                builder.newLayout("PatternLayout")
                    .addAttribute("pattern", pattern)
            )

        val rootLogger = builder.newRootLogger(Level.ALL)
        rootLogger.add(builder.newAppenderRef("File"))

        builder.add(appenderBuilder)
        builder.add(rootLogger)

        context.start(builder.build())

        this.logger = context.getLogger(javaClass)

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