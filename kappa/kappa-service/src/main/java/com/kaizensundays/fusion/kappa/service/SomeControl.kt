package com.kaizensundays.fusion.kappa.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created: Monday 12/4/2023, 19:17 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class SomeControl {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun execute() {
        logger.info("Ok")
    }

}