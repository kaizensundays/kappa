package com.kaizensundays.fusion.kappa.os

import com.zaxxer.nuprocess.NuProcess
import org.jvnet.winp.WinProcess
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 8/28/2022, 12:49 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KappaNuProcess(private val process: NuProcess, private val builder: KappaNuProcessBuilder) : KappaProcess {

    override fun pid() = process.pid

    override fun isRunning() = process.isRunning

    override fun toString(): String {
        return "KappaProcess(${pid()}:${isRunning()})"
    }

    override fun shutdown(timeout: Long, timeUnit: TimeUnit) {
        if (builder.isWindows()) {
            println("Stopping WinProcess...")
            val stopped = WinProcess(process.pid).sendCtrlC()
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
            destroy()
        }
        return code
    }

    override fun destroy() = process.destroy(true)

}