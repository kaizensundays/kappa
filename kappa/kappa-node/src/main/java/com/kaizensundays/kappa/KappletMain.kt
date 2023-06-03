@file:Suppress("unused")

package com.kaizensundays.kappa

import org.springframework.context.annotation.AnnotationConfigApplicationContext

/**
 * Created: Monday 9/5/2022, 1:40 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
object KappletMain {

    @JvmStatic
    fun main(args: Array<String>) {

        val context = AnnotationConfigApplicationContext(KappletContext::class.java)
        context.registerShutdownHook()

    }

}
