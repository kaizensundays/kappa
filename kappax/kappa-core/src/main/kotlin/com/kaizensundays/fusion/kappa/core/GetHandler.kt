package com.kaizensundays.fusion.kappa.core

import com.kaizensundays.fusion.kappa.core.api.GetRequest
import com.kaizensundays.fusion.kappa.core.api.GetResponse
import com.kaizensundays.fusion.kappa.core.api.Handler

/**
 * Created: Sunday 4/28/2024, 1:32 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class GetHandler : Handler<GetRequest, GetResponse> {

    override fun handle(request: GetRequest): GetResponse {
        return request.response()
    }

}