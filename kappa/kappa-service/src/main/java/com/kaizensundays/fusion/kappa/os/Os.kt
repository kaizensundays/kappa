package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.service.Result
import com.zaxxer.nuprocess.NuAbstractProcessHandler
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 9/25/2022, 1:06 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@SuppressWarnings("kotlin:S6518")
abstract class Os {

    abstract fun getPID(): Int

    class ProcessHandler : NuAbstractProcessHandler() {
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

    open fun execute(command: List<String>, timeoutSec: Long): Result {

        val ph = Windows.ProcessHandler()

        val process = KappaNuProcessBuilder()
            .setCommand(command)
            .setProcessListener(ph)
            .start()

        val code = process.waitFor(timeoutSec, TimeUnit.SECONDS)

        return if (ph.error.isNotBlank()) Result(code, ph.error) else Result(code, ph.result)
    }

    abstract fun findPID(serviceId: String): Int

    abstract fun shutdown(pid: Int): String

}