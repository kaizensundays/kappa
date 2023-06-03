package com.kaizensundays.fusion.kappa

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Created: Saturday 10/8/2022, 12:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Configuration
open class EasyBoxContext {

    @Bean
    open fun box(): EasyBox {
        return EasyBox()
    }

}