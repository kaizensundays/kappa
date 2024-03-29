package com.kaizensundays.fusion.kappa.service

import com.kaizensundays.fusion.messaging.Instance

/**
 * Created: Wednesday 6/14/2023, 8:03 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
data class Configuration(
    var hosts: List<Instance> = emptyList(),
)