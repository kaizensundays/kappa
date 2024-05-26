package com.kaizensundays.fusion.kappa.core

import com.kaizensundays.fusion.kappa.core.api.ResponseCode
import com.kaizensundays.fusion.kappa.core.api.Ping
import com.kaizensundays.fusion.kappa.core.api.PingResponse

/**
 * Created: Sunday 7/2/2023, 7:30 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
open class PingHandler : AbstractHandler<Ping, PingResponse>() {

    override fun doHandle(request: Ping): PingResponse {
        logger.info("< $request")

        return response(ResponseCode.Ok, request)
    }

}