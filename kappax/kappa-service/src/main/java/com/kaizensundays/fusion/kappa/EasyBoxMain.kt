package com.kaizensundays.fusion.kappa

import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * Created: Saturday 10/8/2022, 12:30 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
object EasyBoxMain {

    @JvmStatic
    fun main(args: Array<String>) {

        val context = AnnotationConfigApplicationContext(EasyBoxContext::class.java)
        context.registerShutdownHook()

    }

}