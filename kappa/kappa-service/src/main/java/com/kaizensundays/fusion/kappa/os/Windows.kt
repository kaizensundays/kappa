package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.Kappa
import com.sun.jna.platform.win32.Kernel32
import com.zaxxer.nuprocess.NuAbstractProcessHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 9/25/2022, 1:07 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@SuppressWarnings("kotlin:S6518")
class Windows : Os() {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun getPID(): Int {
        return Kernel32.INSTANCE.GetCurrentProcessId()
    }

    class LocalProcessHandler : NuAbstractProcessHandler() {
        var result = ""
        var error = ""

        private fun asString(buffer: ByteBuffer): String {
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            return String(bytes)
        }

        override fun onStdout(buffer: ByteBuffer, closed: Boolean) {
            result += asString(buffer)
        }

        override fun onStderr(buffer: ByteBuffer, closed: Boolean) {
            error += asString(buffer)
        }
    }

    fun serviceTagRegex(tag: String, prefix: String = ".*", suffix: String = ".*"): String {
        return (prefix + tag.replaceFirstChar { c -> "[$c]" } + suffix)
    }

    private fun parsePID(s: String): Int {
        return try {
            Integer.parseInt(s.trim())
        } catch (e: Exception) {
            0
        }
    }

    override fun findPID(serviceId: String): Int {

        val serviceTagRegex = serviceTagRegex(Kappa.serviceTagPrefix + serviceId, "%%", "%%")

        val command =
            """cmd /C wmic process where (commandline like '${serviceTagRegex}') get processid | more +1""".split(" ")

        logger.info("command={}", command)

        val ph = LocalProcessHandler()

        val process = NuProcessBuilderImpl()
            .setCommand(command)
            .setProcessListener(ph)
            .start()

        process.waitFor(20, TimeUnit.SECONDS)

        logger.info("result=\n{}\n---", ph.result)

        val pid = parsePID(ph.result)

        logger.info("PID={}", pid)

        if (ph.error.isNotBlank()) {
            logger.info("error={}", ph.error)
        }

        return pid
    }

    override fun shutdown(pid: Int): String {

        val command = """cmd /C taskkill /T /F /PID $pid""".split(" ")

        val ph = LocalProcessHandler()

        val process = NuProcessBuilderImpl()
            .setCommand(command)
            .setProcessListener(ph)
            .start()

        process.waitFor(20, TimeUnit.SECONDS)

        logger.info("result={}", ph.result)

        return ph.result
    }

}