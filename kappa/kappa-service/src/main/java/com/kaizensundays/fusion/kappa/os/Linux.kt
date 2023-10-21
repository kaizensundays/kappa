package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.unsupportedOperation

/**
 * Created: Saturday 11/26/2022, 11:33 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
open class Linux : Os() {

    override fun getPID(): Int {
        return LinuxCLibrary.INSTANCE.getpid()
    }

    override fun findPID(serviceId: String): Int {
        unsupportedOperation()
    }

    override fun shutdown(pid: Int): String {
        unsupportedOperation()
    }

}