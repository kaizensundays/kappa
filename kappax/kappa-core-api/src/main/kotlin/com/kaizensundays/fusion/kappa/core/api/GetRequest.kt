package com.kaizensundays.fusion.kappa.core.api

/**
 * Created: Sunday 4/28/2024, 1:30 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class GetRequest : Request<GetResponse>() {

    override fun response(): GetResponse {
        return GetResponse(emptyMap())
    }

}