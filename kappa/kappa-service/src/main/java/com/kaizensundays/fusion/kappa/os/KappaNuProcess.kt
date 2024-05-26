package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.isWindows
import com.kaizensundays.fusion.kappa.os.api.KappaProcess
import org.jvnet.winp.WinProcess
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 8/28/2022, 12:49 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KappaNuProcess(private val process: KappaProcess) : KappaProcess {

    override fun pid() = process.pid()

    override fun isRunning() = process.isRunning()

    override fun toString(): String {
        return "KappaProcess(${pid()}:${isRunning()})"
    }

    override fun shutdown(timeout: Long, timeUnit: TimeUnit) {
        if (isWindows()) {
            println("Stopping WinProcess...")
            val stopped = WinProcess(process.pid()).sendCtrlC()
            println("stopped=$stopped")
            if (!stopped) {
                process.destroy(false)
            }
        } else {
            process.destroy(false)
        }
        println("isRunning=${isRunning()}")
        process.waitFor(timeout, timeUnit)
    }

    override fun waitFor(timeout: Long, timeUnit: TimeUnit): Int {
        val code = process.waitFor(timeout, timeUnit)
        if (code != 0) {
            destroy(true)
        }
        return code
    }

    override fun destroy(force: Boolean) = process.destroy(force)

}