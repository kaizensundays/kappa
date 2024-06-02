package com.kaizensundays.fusion.kappa

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created: Sunday 6/2/2024, 12:56 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class SomeTest {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Test
    fun test() {
        logger.info("?")
    }

}
