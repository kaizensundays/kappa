package com.kaizensundays.fusion.kappa.plugin.core

import com.kaizensundays.fusion.kappa.os.JDKProcessBuilder
import com.kaizensundays.fusion.kappa.plugin.AbstractKappaMojo
import com.kaizensundays.fusion.kappa.core.Deployments
import kotlinx.coroutines.runBlocking
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.util.concurrent.TimeUnit

/**
 * Created: Saturday 1/6/2024, 5:20 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "run", defaultPhase = LifecyclePhase.NONE)
class RunMojo : AbstractKappaMojo() {

    private val deployments = Deployments()

    override fun doExecute() {

        val fileName = System.getProperty("file", "")
        println("fileName=$fileName")

        val serviceMap = runBlocking { deployments.readAndValidateDeployment(fileName) }
        println("serviceMap=$serviceMap")

        val service = serviceMap.values.first()
        println("service=$service")

        val builder = JDKProcessBuilder()

        val process = builder.setCommand(service.command).start()

        val exitCode = process.waitFor(10, TimeUnit.SECONDS)
        println("exitCode=$exitCode")

        println("Done")
    }

}