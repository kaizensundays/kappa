package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.messsaging.Instance
import com.kaizensundays.fusion.messsaging.LoadBalancer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created: Saturday 10/14/2023, 12:27 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class DefaultLoadBalancer(private val instance: Instance) : LoadBalancer {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun get(): Instance {
        logger.info("{}", instance)
        return instance
    }

}