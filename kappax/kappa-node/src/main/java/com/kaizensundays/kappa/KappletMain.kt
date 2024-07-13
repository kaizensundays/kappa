package com.kaizensundays.kappa

import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * Created: Monday 9/5/2022, 1:40 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
object KappletMain {

    @JvmStatic
    fun main(args: Array<String>) {

        val context = ClassPathXmlApplicationContext("KappletContextConfig.xml", "KappletContext.xml")
        context.registerShutdownHook()

    }

}
