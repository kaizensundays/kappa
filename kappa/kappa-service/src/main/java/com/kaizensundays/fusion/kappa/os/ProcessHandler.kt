package com.kaizensundays.fusion.kappa.os

import com.zaxxer.nuprocess.NuAbstractProcessHandler

/**
 * Created: Saturday 4/20/2024, 6:40 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class ProcessHandler : NuAbstractProcessHandler() {

    open fun onStart(process: Process) {
        //
    }

    open fun onExit() {
        //
    }

}