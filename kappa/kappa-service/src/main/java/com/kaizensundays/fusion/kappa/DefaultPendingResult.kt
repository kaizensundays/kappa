package com.kaizensundays.fusion.kappa

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * Created: Saturday 7/8/2023, 12:22 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class DefaultPendingResult<T> : PendingResult<T> {

    private val queue = ArrayBlockingQueue<T>(1)

    override fun toString(): String {
        return "PendingResult($queue)"
    }

    override fun get(timeout: Long, unit: TimeUnit): T? {
        return queue.poll(timeout, unit)
    }

    override fun put(t: T): Boolean {
        return queue.offer(t)
    }

}