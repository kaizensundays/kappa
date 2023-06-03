package com.kaizensundays.fusion.kappa.plugin

import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo

/**
 * Created: Saturday 12/3/2022, 12:03 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Mojo(name = "stop-kapplet", defaultPhase = LifecyclePhase.NONE)
class StopKappletMojo : StopMojo() {

    override fun urlPath(serviceId: String): String {
        return "/stop-kapplet"
    }

}