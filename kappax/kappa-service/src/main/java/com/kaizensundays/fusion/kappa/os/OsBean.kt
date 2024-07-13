package com.kaizensundays.fusion.kappa.os

import com.kaizensundays.fusion.kappa.core.api.isWindows
import org.springframework.beans.factory.FactoryBean

class OsBean : FactoryBean<Os> {

    override fun getObject(): Os {
        return if (isWindows()) Windows() else Linux()
    }

    override fun getObjectType(): Class<*> {
        return Os::class.java
    }

}