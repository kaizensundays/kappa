package com.kaizensundays.fusion.kappa.os

import com.zaxxer.nuprocess.NuProcessHandler
import java.nio.file.Path
import java.util.*

/**
 * Created: Saturday 10/8/2022, 1:25 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
interface OSProcessBuilder {

    fun isWindows(props: Properties): Boolean

    fun isWindows(): Boolean

    fun setCommand(command: List<String>): OSProcessBuilder

    fun setWorkingDir(workingDir: Path): OSProcessBuilder

    fun setEnvironment(environment: Map<String, String>): OSProcessBuilder

    fun setProcessListener(listener: NuProcessHandler): OSProcessBuilder

    fun setJdk(jdk: Boolean): OSProcessBuilder

    fun start(): KappaProcess
}