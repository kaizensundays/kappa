package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.service.Service
import com.kaizensundays.fusion.kappa.isWindows
import com.kaizensundays.fusion.kappa.unsupportedOperation

/**
 * Created: Sunday 6/4/2023, 1:08 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class CommandBuilder {

    companion object {

        fun command(service: Service, env: Map<String, String>, isWindows: Boolean, set: CommandBuilder.() -> Unit): List<String> {
            val builder = if (isWindows) WindowsCommandBuilder(service, env) else LinuxCommandBuilder(service, env)
            builder.set()
            return builder.build()
        }

        fun command(service: Service, env: Map<String, String>, set: CommandBuilder.() -> Unit): List<String> {
            return command(service, env, isWindows(), set)
        }

    }

    var artifactMap: Map<String, String> = emptyMap()
    var serviceTagPrefix = ""
    var serviceId = ""
    var commandTarget = CommandTarget.Default

    protected fun jar(artifact: String?): String {
        if (artifact != null) {
            val jar = artifactMap[artifact]
            if (jar != null) {
                return "$jar "
            }
        }
        return ""
    }

    open fun build(command: String): List<String> = unsupportedOperation()

    abstract fun build(): List<String>

}