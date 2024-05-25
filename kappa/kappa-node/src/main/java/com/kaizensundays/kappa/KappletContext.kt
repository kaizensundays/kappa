package com.kaizensundays.kappa

import com.kaizensundays.fusion.kappa.KappletKtorServer
import com.kaizensundays.fusion.kappa.cast
import com.kaizensundays.fusion.kappa.core.api.Handler
import com.kaizensundays.fusion.kappa.core.api.Request
import com.kaizensundays.fusion.kappa.core.api.Service
import com.kaizensundays.fusion.kappa.isWindows
import com.kaizensundays.fusion.kappa.os.Linux
import com.kaizensundays.fusion.kappa.os.NuProcessBuilderImpl
import com.kaizensundays.fusion.kappa.os.Os
import com.kaizensundays.fusion.kappa.os.Windows
import com.kaizensundays.fusion.kappa.service.Kapplet
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.PropertySource
import javax.cache.Cache

/**
 * Created: Saturday 9/10/2022, 1:17 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Configuration
@PropertySource("classpath:/application.properties")
@ImportResource("classpath:KappletContextConfig.xml", "classpath:KappletContext.xml")
open class KappletContext {

    @Value("\${kapplet.server.port}")
    var serverPort = 0

    @Bean
    open fun os(): Os {
        return if (isWindows()) Windows() else Linux()
    }

    @Bean
    open fun kapplet(os: Os, serviceStore: Cache<String, String>, @Qualifier("handlers") handlers: Map<Class<out Request<*>>, Handler<*, *>>, serviceCache: Cache<String, Service>): Kapplet {
        @Suppress("UNCHECKED_CAST")
        val kapplet = Kapplet(os, NuProcessBuilderImpl(), serviceStore, serviceCache, handlers.cast())
        kapplet.enabled = false
        return kapplet
    }

    @Bean
    open fun ktorServer(os: Os, kapplet: Kapplet) = KappletKtorServer(serverPort, os, kapplet)

}