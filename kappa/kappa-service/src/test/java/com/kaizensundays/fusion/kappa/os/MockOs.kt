package com.kaizensundays.fusion.kappa.os

import java.util.*
import kotlin.math.max

/**
 * Created: Saturday 12/17/2022, 12:08 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class MockOs : Os {

    private val initialPid = 30_000

    private val processMap = mutableMapOf<Int, MockProcess>()

    fun add(process: MockProcess) {
        processMap[process.pid] = process
    }

    fun getProcesses(): Map<Int, MockProcess> {
        return TreeMap(processMap)
    }

    fun nextPID(): Int {
        val pid = if (processMap.isEmpty()) 0 else processMap.keys.maxOf { it } + 1
        return max(pid, initialPid)
    }

    override fun getPID(): Int {
        return 100
    }

    override fun findPID(serviceId: String): Int {
        val process = processMap.values.find { process -> process.cmd.contains(serviceId) }
        return process?.pid ?: 0
    }

    override fun shutdown(pid: Int): String {
        return "?"
    }

}