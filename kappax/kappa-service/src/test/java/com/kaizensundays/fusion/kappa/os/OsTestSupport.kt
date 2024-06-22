package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.core.api.isWindows
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created: Saturday 12/9/2023, 6:26 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class OsTestSupport {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun command(): List<String> {
        return if (isWindows())
            listOf("cmd", "/c", "dir", "*.xml")
        else
            listOf("/bin/bash", "-c", "ls *.xml")
    }

}