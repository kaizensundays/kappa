package com.kaizensundays.fusion.kappa

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Created: Saturday 10/8/2022, 12:37 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class EasyBox {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    private val timer = Timer()

    @PostConstruct
    fun start() {

        timer.schedule(object : TimerTask() {
            override fun run() {
                logger.info("{}", System.currentTimeMillis())
            }
        }, 3000, 3000)


        logger.info("Started")
    }

    @PreDestroy
    fun stop() {

        timer.cancel()

        logger.info("Stopped")
    }

}