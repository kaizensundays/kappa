package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Apply
import com.kaizensundays.fusion.kappa.Kappa
import com.kaizensundays.fusion.kappa.Kapplet
import kotlinx.coroutines.runBlocking
import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.io.File

/**
 * Created: Sunday 11/13/2022, 11:17 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "start-kapplet", defaultPhase = LifecyclePhase.NONE)
class StartKappletMojo : AbstractKappaMojo() {

    private val classpathDir = "./.kappa/lib"

    private fun populateLib() {

        val pluginDescriptor = this.pluginContext["pluginDescriptor"] as PluginDescriptor

        val srcFiles = pluginDescriptor.artifacts
            .map { artifact -> artifact.file }

        logger.info("srcFiles={}", srcFiles.size)

        val targetDir = File(classpathDir)

        targetDir.mkdirs()

        logger.info("Deleting files:")

        targetDir.listFiles()?.forEach { file ->
            logger.info("{}", file.name)
            file.delete()
        }

        logger.info("Copying files:")

        srcFiles.forEach { srcFile ->
            try {
                logger.info("{}", srcFile.name)
                val targetFile = File(classpathDir + "/" + srcFile.name)
                srcFile.copyTo(targetFile)
            } catch (e: Exception) {
                logger.error(e.message, e)
            }
        }
    }

    private fun deployKapplet() {

        val kapplet = context.getBean(Kapplet::class.java)

        val serviceMap = runBlocking { kapplet.doApply(Apply("/kapplet-lib.yaml", emptyMap())) }

        logger.info("Ok\n$serviceMap")

        Thread.sleep(1000)

        val pid = kapplet.findPID("1234567")

        logger.info("PID=$pid")

        Thread.sleep(1000)
    }

    override fun doExecute() {
        super.init()

        val conf = getConfiguration()
        println("$conf\n")

        val hostPort = conf.hosts.first()

        if (kappletIsNotRunning(hostPort)) {
            println("Kapplet is not running")

            populateLib()

            deployKapplet()
        }

        checkKapplet(hostPort)
    }

}