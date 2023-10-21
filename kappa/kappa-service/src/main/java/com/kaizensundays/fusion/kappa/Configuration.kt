package com.kaizensundays.fusion.kappa

import com.kaizensundays.fusion.messsaging.Instance

/**
 * Created: Wednesday 6/14/2023, 8:03 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
data class Configuration(
    var hosts: List<Instance> = emptyList(),
)