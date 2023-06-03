package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.unsupportedOperation
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 10/9/2022, 12:52 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class WindowsJNAProcess(private val wpg: WindowsProcessGroup, private val pid: Int) : KappaProcess {

    override fun pid(): Int {
        return pid
    }

    override fun isRunning(): Boolean {
        unsupportedOperation()
    }

    override fun shutdown(timeout: Long, timeUnit: TimeUnit) {
        unsupportedOperation()
    }

    override fun waitFor(timeout: Long, timeUnit: TimeUnit): Int {
        unsupportedOperation()
    }

    override fun destroy() {
        wpg.destroy()
    }

}