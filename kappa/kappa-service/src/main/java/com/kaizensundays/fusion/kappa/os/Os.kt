package com.kaizensundays.fusion.kappa.os

/**
 * Created: Sunday 9/25/2022, 1:06 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface Os {

    fun getPID(): Int

    fun findPID(serviceId: String): Int

    fun shutdown(pid: Int): String

}