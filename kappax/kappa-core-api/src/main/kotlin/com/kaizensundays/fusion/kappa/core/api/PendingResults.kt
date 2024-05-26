package com.kaizensundays.fusion.kappa.core.api

/**
 * Created: Saturday 7/8/2023, 12:20 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface PendingResults<T> {

    fun get(requestId: String): PendingResult<T>

    fun remove(requestId: String): PendingResult<T>?

    fun size(): Int

}