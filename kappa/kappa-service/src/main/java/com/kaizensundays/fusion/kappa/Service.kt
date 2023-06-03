package com.kaizensundays.fusion.kappa

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.kaizensundays.fusion.kappa.os.KappaProcess
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Created: Sunday 8/28/2022, 1:19 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Service(
    var name: String,
    var nameSuffix: String? = null,
    var artifact: String? = null,
    var command: MutableList<String> = mutableListOf(),
    var stopCommand: MutableList<String> = mutableListOf(),
    var env: Map<String, String> = mapOf(),
    var jvmOptions: List<String> = listOf(),
    var mainClass: String = "",
    var workingDir: String? = null,
    var hasConsole: String = "false",
    var pid: Int = 0
) {
    @JsonIgnore
    @Transient
    var process: KappaProcess? = null
}