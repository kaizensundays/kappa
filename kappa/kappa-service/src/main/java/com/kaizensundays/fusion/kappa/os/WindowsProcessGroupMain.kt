package com.kaizensundays.fusion.kappa.os

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep

/**
 * Created: Sunday 9/18/2022, 1:28 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class WindowsProcessGroupMain {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    fun execute() {
        logger.info("execute() >")


        val command = listOf(
            /*
                        "cmd", "/C", "start",
            */
            "java", "-cp", "kappa.jar",
            "-Dcom.kaizensundays.kappa.service.id.1234567",
            "-Dlog4j.configurationFile=easybox-log4j2.xml",
            "-Dlog4j.shutdownHookEnabled=false",
            "-Dloader.main=com.kaizensundays.fusion.kappa.EasyBoxMain",
            "org.springframework.boot.loader.PropertiesLauncher"
        )

        val pb = WindowsJNAProcessBuilder()
        pb.setCommand(command)
        val process = pb.start()

        val pid = process.pid()

        logger.info("started PID=${pid}")

        sleep(30000)

        process.destroy()

        logger.info("execute() <")
    }

}

fun main(args: Array<String>) {

    println(args)

    WindowsProcessGroupMain().execute()
}
