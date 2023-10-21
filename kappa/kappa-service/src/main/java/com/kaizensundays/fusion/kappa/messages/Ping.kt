package com.kaizensundays.fusion.kappa.messages

import com.kaizensundays.fusion.kappa.event.Request

/**
 * Created: Sunday 7/2/2023, 1:11 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class Ping : Request<PingResponse>() {

    override fun response() = PingResponse()

    override fun toString(): String {
        return "Ping{}"
    }
}
