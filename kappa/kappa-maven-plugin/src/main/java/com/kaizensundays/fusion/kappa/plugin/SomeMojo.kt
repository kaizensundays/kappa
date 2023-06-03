package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Apply
import com.kaizensundays.fusion.kappa.Kapplet
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.lang.Thread.sleep

/**
 * Created: Friday 11/11/2022, 10:24 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "some", defaultPhase = LifecyclePhase.NONE)
class SomeMojo : AbstractKappaMojo() {

    override fun doExecute() {
        super.init()

        val kapplet = context.getBean(Kapplet::class.java)

        val serviceMap = runBlocking {
            withTimeout(10000L) {
                kapplet.doApply(Apply("/easybox.yaml", emptyMap()))
            }
        }

        logger.info("Ok $serviceMap")

        sleep(1000)

        val pid = kapplet.findPID("1234567")

        logger.info("PID=$pid")

        sleep(1000)
    }

}