package com.kaizensundays.fusion.kappa.plugin

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created: Sunday 10/15/2023, 1:15 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class LoggingTest {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val prop = "log.to.system.out"

    private fun Logger.log(msg: String) {
        if ("true" == System.getProperty(prop, "").lowercase()) {
            println(msg)
        } else {
            this.info(msg)
        }
    }

    @Test
    fun test() {
        val msg = "handle() ${System.currentTimeMillis()}"

        logger.log(msg)

        System.setProperty(prop, "true")

        logger.log(msg)
    }

}