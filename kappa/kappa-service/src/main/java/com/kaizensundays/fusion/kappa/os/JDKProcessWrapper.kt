package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.os.api.KappaProcess
import com.kaizensundays.fusion.kappa.core.api.unsupportedOperation
import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 12/3/2023, 5:38 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JDKProcessWrapper(private val process: Process) : KappaProcess {

    override fun pid(): Int {
        return process.pid().toInt()
    }

    override fun isRunning(): Boolean {
        return process.isAlive
    }

    override fun shutdown(timeout: Long, timeUnit: TimeUnit) {
        unsupportedOperation()
    }

    override fun waitFor(timeout: Long, timeUnit: TimeUnit): Int {
        return if (process.waitFor(timeout, timeUnit)) process.exitValue() else -1
    }

    override fun destroy(force: Boolean) {
        process.destroy()
    }

}