package com.kaizensundays.fusion.kappa.service

import java.util.concurrent.TimeUnit

/**
 * Created: Sunday 7/9/2023, 12:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface PendingResult<T> {

    fun get(timeout: Long, unit: TimeUnit): T?

    fun put(t: T): Boolean

}