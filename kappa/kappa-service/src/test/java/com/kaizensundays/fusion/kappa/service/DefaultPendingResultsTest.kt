package com.kaizensundays.fusion.kappa.service

import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

/**
 * Created: Saturday 7/8/2023, 12:23 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class DefaultPendingResultsTest {

    private val pendingResults = DefaultPendingResults<Int>()

    private fun thread() = '(' + Thread.currentThread().name + ')'

    @Test
    fun get() {

        val num = 8

        val executor = Executors.newFixedThreadPool(16)

        val results = HashSet<PendingResult<Int>>()

        val latch = CountDownLatch(num)

        executor.execute {
            repeat(num) { i ->
                println("${thread()} [$i]")
                results.add(pendingResults.get(i.toString()))
            }

            println("${thread()} results.size=" + results.size)

            // get results
            results.forEach { result ->
                val value = result.get(10, TimeUnit.SECONDS)
                println("${thread()} get() $value")
                latch.countDown()
            }
        }

        // put results
        repeat(num) { i ->
            executor.execute {
                val result = pendingResults.get(i.toString())
                val value = i * 10 + 1
                result.put(value)
                println("${thread()} put($i:$value)")
            }
        }

        latch.await()

        assertEquals(num, pendingResults.size())

        repeat(num) { i -> pendingResults.remove(i.toString())}

        assertEquals(0, pendingResults.size())

        println("Done")
    }

}