package com.kaizensundays.fusion.kappa

import kotlinx.serialization.Serializable

/**
 * Created: Sunday 1/15/2023, 12:31 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Serializable
data class Apply(val fileName: String, val artifacts: Map<String, String>)