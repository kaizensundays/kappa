package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.service.Result
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created: Saturday 11/26/2022, 11:33 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
open class Linux : Os() {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun getPID(): Int {
        return LinuxCLibrary.INSTANCE.getpid()
    }

    fun parsePID(result: Result): Int {
        return if (result.code == 0) {
            try {
                Integer.parseInt(
                    result.text
                        .replace("\"", "")
                        .replace("\n", "")
                )
            } catch (e: Exception) {
                -999
            }
        } else {
            -result.code
        }
    }

    override fun findPID(serviceId: String): Int {
        val result = execute(
            listOf(
                "/bin/bash", "-c",
                "ps -eo pid,args | grep $serviceId | awk '{print \$1}' | head -n 1"
            ), 10
        )
        println("result=$result")
        return parsePID(result)
    }

    override fun shutdown(pid: Int): String {
        val result = execute(
            listOf(
                "/bin/bash", "-c", "kill $pid"
            ), 10
        )
        return result.toString()
    }

}