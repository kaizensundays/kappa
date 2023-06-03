package com.kaizensundays.fusion.kappa

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import kotlinx.serialization.Serializable

/**
 * Created: Sunday 5/21/2023, 12:06 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Node(
    val id: String,
    val ip: String,
    val status: Int
)
