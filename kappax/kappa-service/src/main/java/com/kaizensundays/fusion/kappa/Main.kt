package com.kaizensundays.fusion.kappa

import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * Created: Sunday 8/21/2022, 12:42 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
object Main {

    @JvmStatic
    fun main(args: Array<String>) {

        val context = AnnotationConfigApplicationContext(KappaContext::class.java)
        context.registerShutdownHook()

    }

}