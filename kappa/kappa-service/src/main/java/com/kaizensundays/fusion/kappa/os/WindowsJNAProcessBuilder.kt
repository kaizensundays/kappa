package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.unsupportedOperation
import com.zaxxer.nuprocess.NuProcessHandler
import java.io.File
import java.nio.file.Path
import java.util.*

/**
 * Created: Sunday 10/9/2022, 12:46 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class WindowsJNAProcessBuilder : OSProcessBuilder {

    private var command: List<String> = emptyList()
    private var workingDir = File(".").toPath()
    private var environment: Map<String, String> = emptyMap()
    private var processHandler: NuProcessHandler = NoConsoleProcessHandler()

    override fun isWindows(props: Properties): Boolean {
        unsupportedOperation()
    }

    override fun isWindows(): Boolean {
        unsupportedOperation()
    }

    override fun setCommand(command: List<String>): OSProcessBuilder {
        this.command = command
        return this
    }

    override fun setWorkingDir(workingDir: Path): OSProcessBuilder {
        this.workingDir = workingDir
        return this
    }

    override fun setEnvironment(environment: Map<String, String>): OSProcessBuilder {
        this.environment = environment
        return this
    }

    override fun setProcessListener(listener: NuProcessHandler): OSProcessBuilder {
        this.processHandler = listener
        return this
    }

    override fun start(): KappaProcess {

        if (command.isEmpty()) {
            throw IllegalArgumentException()
        }

        val args: Array<String> = if (command.size > 1)
            command.subList(1, command.size).toTypedArray()
        else
            emptyArray()

        val wpg = WindowsProcessGroup(command[0], *args)

        val pid = wpg.executeAsync()

        return WindowsJNAProcess(wpg, pid)
    }

}