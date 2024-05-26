package com.kaizensundays.fusion.kappa.os.api

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

    fun setConsole(fileName: String, pattern: String): OSProcessBuilder

    fun setProcessListener(listener: Any): OSProcessBuilder

    fun start(): KappaProcess
}