package com.kaizensundays.fusion.kappa.core.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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