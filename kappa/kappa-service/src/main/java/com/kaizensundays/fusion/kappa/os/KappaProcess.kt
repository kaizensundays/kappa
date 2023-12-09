package com.kaizensundays.fusion.kappa.os

import java.util.concurrent.TimeUnit

/**
 * Created: Saturday 10/8/2022, 1:23 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface KappaProcess {

    fun pid(): Int

    fun isRunning(): Boolean

    fun shutdown(timeout: Long, timeUnit: TimeUnit)

    fun waitFor(timeout: Long, timeUnit: TimeUnit): Int

    fun destroy(force: Boolean)

}