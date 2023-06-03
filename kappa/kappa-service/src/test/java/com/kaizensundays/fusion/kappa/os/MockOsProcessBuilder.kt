package com.kaizensundays.fusion.kappa.os

import com.zaxxer.nuprocess.NuProcessHandler
import java.nio.file.Path
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created: Saturday 12/17/2022, 12:39 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class MockOsProcessBuilder(private val os: MockOs) : OSProcessBuilder {

    private var command: List<String> = emptyList()

    override fun isWindows(props: Properties): Boolean {
        return false
    }

    override fun isWindows(): Boolean {
        return false
    }

    override fun setCommand(command: List<String>): OSProcessBuilder {
        this.command = command
        return this
    }

    override fun setWorkingDir(workingDir: Path): OSProcessBuilder {
        return this
    }

    override fun setEnvironment(environment: Map<String, String>): OSProcessBuilder {
        return this
    }

    override fun setProcessListener(listener: NuProcessHandler): OSProcessBuilder {
        return this
    }

    override fun start(): KappaProcess {

        val pid = os.nextPID()

        val cmd = command.joinToString(" ")

        val mockProcess = MockProcess(pid, cmd)

        os.add(mockProcess)

        return object : KappaProcess {
            override fun pid(): Int {
                return pid
            }

            override fun isRunning(): Boolean {
                return false
            }

            override fun shutdown(timeout: Long, timeUnit: TimeUnit) {
                //
            }

            override fun waitFor(timeout: Long, timeUnit: TimeUnit): Int {
                return 0
            }

            override fun destroy() {
                //
            }
        }
    }

}