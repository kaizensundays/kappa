package com.kaizensundays.fusion.kappa.os

import com.zaxxer.nuprocess.NuAbstractProcessHandler
import com.zaxxer.nuprocess.NuProcess
import java.nio.ByteBuffer

/**
 * Created: Saturday 8/20/2022, 12:50 PM Eastern Time
 *
 * @author Brett Wooldridge
 */
@SuppressWarnings("kotlin:S6518")
class ProcessHandler : NuAbstractProcessHandler() {

    private var nuProcess: NuProcess? = null

    override fun onStart(nuProcess: NuProcess) {
        this.nuProcess = nuProcess
    }

    override fun onStdinReady(buffer: ByteBuffer): Boolean {
        buffer.put("Hello world!".toByteArray())
        buffer.flip()
        return false // false means we have nothing else to write at this time
    }

    override fun onStdout(buffer: ByteBuffer, closed: Boolean) {

        if (!closed) {
            val bytes = ByteArray(buffer.remaining())
            // You must update buffer.position() before returning (either implicitly,
            // like this, or explicitly) to indicate how many bytes your handler has consumed.
            buffer.get(bytes)
            print(String(bytes))

            // For this example, we're done, so closing STDIN will cause the "cat" process to exit
            //nuProcess!!.closeStdin(true)
        } else {
            println("onStdout() Closed")

            val bytes = ByteArray(buffer.remaining())
            // You must update buffer.position() before returning (either implicitly,
            // like this, or explicitly) to indicate how many bytes your handler has consumed.
            buffer.get(bytes)
            print(String(bytes))
        }
    }

    override fun onStderr(buffer: ByteBuffer, closed: Boolean) {

        if (!closed) {
            val bytes = ByteArray(buffer.remaining())
            // You must update buffer.position() before returning (either implicitly,
            // like this, or explicitly) to indicate how many bytes your handler has consumed.
            buffer.get(bytes)
            println(String(bytes))

            // For this example, we're done, so closing STDIN will cause the "cat" process to exit
            //nuProcess!!.closeStdin(true)
        } else {
            println("Stderr() Closed")

            val bytes = ByteArray(buffer.remaining())
            // You must update buffer.position() before returning (either implicitly,
            // like this, or explicitly) to indicate how many bytes your handler has consumed.
            buffer.get(bytes)
            println(String(bytes))
        }
    }

}