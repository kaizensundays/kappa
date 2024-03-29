package com.kaizensundays.fusion.kappa.service

import com.kaizensundays.fusion.kappa.event.Request
import com.kaizensundays.fusion.kappa.messages.ApplyResponse
import kotlinx.serialization.Serializable

/**
 * Created: Sunday 1/15/2023, 12:31 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Serializable
data class Apply(
    val fileName: String,
    val artifacts: Map<String, String>,
    val serviceMap: Map<String, Service> = emptyMap()
) : Request<ApplyResponse>() {

    override fun response() = ApplyResponse()

}