package com.kaizensundays.fusion.kappa.service

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Created: Sunday 4/14/2024, 7:20 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@JsonIgnoreProperties(ignoreUnknown = false)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
interface ServiceMixIn {
}