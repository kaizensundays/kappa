package com.kaizensundays.fusion.kappa

import org.junit.jupiter.api.Test

/**
 * Created: Sunday 8/4/2024, 12:06 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KappletClusterContainerTest {

/*
    set ATOMIX_PROFILE=Consensus
    set ATOMIX_BOOTSTRAP=A:localhost:5001;B:localhost:5002;C:localhost:5003
    set ATOMIX_NODE_PORT=5001
    set ATOMIX_NODE_ID=A

    set KAPPLET_SERVER_PORT=7711
    set KAPPLET_WEB_PORT=7713
*/

    val conf = mapOf(
        "A" to mutableMapOf(
            "ATOMIX_PROFILE" to "Consensus",
            "ATOMIX_BOOTSTRAP" to "A:localhost:5001;B:localhost:5002;C:localhost:5003",
            "ATOMIX_NODE_ID" to "A",
            "ATOMIX_NODE_PORT" to "5501",
            "KAPPLET_SERVER_PORT" to "7711",
            "KAPPLET_WEB_PORT" to "7713",
            "KAPPLET_PROPERTIES" to "kapplet.yml"
        ),
        "B" to mutableMapOf(
            "ATOMIX_PROFILE" to "Consensus",
            "ATOMIX_BOOTSTRAP" to "A:localhost:5001;B:localhost:5002;C:localhost:5003",
            "ATOMIX_NODE_ID" to "B",
            "ATOMIX_NODE_PORT" to "5501",
            "KAPPLET_SERVER_PORT" to "7721",
            "KAPPLET_WEB_PORT" to "7723",
            "KAPPLET_PROPERTIES" to "kapplet.yml"
        ),
        "C" to mutableMapOf(
            "ATOMIX_PROFILE" to "Consensus",
            "ATOMIX_BOOTSTRAP" to "A:localhost:5001;B:localhost:5002;C:localhost:5003",
            "ATOMIX_NODE_ID" to "C",
            "ATOMIX_NODE_PORT" to "5501",
            "KAPPLET_SERVER_PORT" to "7711",
            "KAPPLET_WEB_PORT" to "7713",
            "KAPPLET_PROPERTIES" to "kapplet.yml"
        ),
    )

    @Test
    fun test() {
    }
}