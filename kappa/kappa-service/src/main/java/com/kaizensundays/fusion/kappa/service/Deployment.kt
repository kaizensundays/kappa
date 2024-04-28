package com.kaizensundays.fusion.kappa.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.kaizensundays.fusion.kappa.core.api.Service
import kotlinx.serialization.Serializable

/**
 * Created: Sunday 8/28/2022, 1:18 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
class Deployment {

    val services = mutableListOf<Service>()

}