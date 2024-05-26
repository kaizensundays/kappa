package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.os.api.KappaProcess
import com.kaizensundays.fusion.kappa.core.api.unsupportedOperation
import com.zaxxer.nuprocess.NuProcess
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

    override fun shutdown(timeout: Long, timeUnit: TimeUnit) {
        unsupportedOperation()
    }

    override fun waitFor(timeout: Long, timeUnit: TimeUnit): Int {
        return process.waitFor(timeout, timeUnit)
    }

    override fun destroy(force: Boolean) {
        process.destroy(true)
    }

}