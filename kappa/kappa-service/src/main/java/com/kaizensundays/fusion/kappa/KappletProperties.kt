package com.kaizensundays.fusion.kappa

import org.springframework.beans.factory.annotation.Value

/**
 * Created: Saturday 10/1/2022, 12:51 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KappletProperties {

    @Value("\${kapplet.deployerEnabled}")
    var deployerEnabled = false

    @Value("\${kapplet.cacheLocation}")
    var cacheLocation = ""

}