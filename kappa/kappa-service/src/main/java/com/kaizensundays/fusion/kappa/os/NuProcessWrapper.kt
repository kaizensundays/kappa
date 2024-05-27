package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.core.api.isWindows
import com.kaizensundays.fusion.kappa.os.api.KappaProcess
import com.zaxxer.nuprocess.NuProcess
import org.jvnet.winp.WinProcess
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 12/3/2023, 5:18 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class NuProcessWrapper(private val process: NuProcess) : KappaProcess {

    override fun pid(): Int {
        return process.pid
    }

    override fun isRunning(): Boolean {
        return process.isRunning
    }

    override fun toString(): String {
        return "NuProcess(${pid()}:${isRunning()})"
    }

    override fun shutdown(timeout: Long, timeUnit: TimeUnit) {
        if (isWindows()) {
            println("Stopping WinProcess...")
            val stopped = WinProcess(pid()).sendCtrlC()
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

    override fun destroy(force: Boolean) {
        process.destroy(true)
    }

}