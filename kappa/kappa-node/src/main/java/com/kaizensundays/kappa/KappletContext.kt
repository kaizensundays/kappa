package com.kaizensundays.kappa

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource

/**
 * Created: Saturday 9/10/2022, 1:17 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Configuration
@PropertySource("classpath:/application.properties")
@ImportResource("classpath:KappletContextConfig.xml", "classpath:KappletContext.xml")
open class KappletContext {

}