package com.kaizensundays.fusion.kappa.service

import com.kaizensundays.fusion.kappa.cache.InMemoryCache
import com.kaizensundays.fusion.kappa.os.MockOs
import com.kaizensundays.fusion.kappa.os.MockOsProcessBuilder
import com.kaizensundays.fusion.kappa.service.Kapplet
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.cache.Cache

/**
 * Created: Saturday 12/17/2022, 11:41 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Configuration
open class KappletIntegrationTestContext {

    @Bean
    open fun os(): MockOs {
        return MockOs()
    }

    @Bean
    open fun serviceCache(): Cache<String, String> {
        return InMemoryCache()
    }

    @Bean
    open fun kapplet(os: MockOs, serviceCache: Cache<String, String>): Kapplet {
        return Kapplet(os, MockOsProcessBuilder(os), serviceCache, emptyMap())
    }

}