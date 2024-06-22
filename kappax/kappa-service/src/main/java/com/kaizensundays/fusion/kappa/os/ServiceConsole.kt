package com.kaizensundays.fusion.kappa.os

import com.zaxxer.nuprocess.NuProcess
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 3/26/2023, 2:44 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@SuppressWarnings("kotlin:S6518")
class ServiceConsole(private val fileName: String, private val pattern: String) : ProcessHandler() {

    private var nuProcess: NuProcess? = null

    lateinit var logger: Logger

    private val context = LoggerContext("ConsoleLoggerContext")

    fun getLogger(cls: Class<*>): Logger {
        return context.getLogger(cls)
    }

    override fun onStart(nuProcess: NuProcess?) {
        this.nuProcess = nuProcess

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
    }

    override fun onExit(statusCode: Int) {
        context.stop(10, TimeUnit.SECONDS)
    }

    override fun onStdout(buffer: ByteBuffer, closed: Boolean) {
        if (!closed) {
            val bytes = ByteArray(buffer.remaining())
            // You must update buffer.position() before returning (either implicitly,
            // like this, or explicitly) to indicate how many bytes your handler has consumed.
            buffer.get(bytes)
            logger.info(String(bytes))

            // For this example, we're done, so closing STDIN will cause the "cat" process to exit
            //nuProcess!!.closeStdin(true)
        } else {
            logger.info("onStdout() Closed")

            val bytes = ByteArray(buffer.remaining())
            // You must update buffer.position() before returning (either implicitly,
            // like this, or explicitly) to indicate how many bytes your handler has consumed.
            buffer.get(bytes)
            logger.info(String(bytes))
        }
    }

    override fun onStderr(buffer: ByteBuffer, closed: Boolean) {
        if (!closed) {
            val bytes = ByteArray(buffer.remaining())
            // You must update buffer.position() before returning (either implicitly,
            // like this, or explicitly) to indicate how many bytes your handler has consumed.
            buffer.get(bytes)
            logger.info(String(bytes))

            // For this example, we're done, so closing STDIN will cause the "cat" process to exit
            //nuProcess!!.closeStdin(true)
        } else {
            logger.info("Stderr() Closed")

            val bytes = ByteArray(buffer.remaining())
            // You must update buffer.position() before returning (either implicitly,
            // like this, or explicitly) to indicate how many bytes your handler has consumed.
            buffer.get(bytes)
            logger.info(String(bytes))
        }
    }

}