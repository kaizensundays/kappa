package com.kaizensundays.fusion.kappa

import java.util.concurrent.ConcurrentHashMap

/**
 * Created: Saturday 7/8/2023, 12:21 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class DefaultPendingResults<T> : PendingResults<T> {

    private val results = ConcurrentHashMap<String, DefaultPendingResult<T>>()

    override fun get(requestId: String): PendingResult<T> {
        return results.computeIfAbsent(requestId) { DefaultPendingResult() }
    }

    override fun remove(requestId: String) = results.remove(requestId)

    override fun size() = results.size

}