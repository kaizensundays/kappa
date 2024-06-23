package com.kaizensundays.fusion.kappa.core.api

/**
 * Created: Saturday 12/26/2020, 4:42 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class Request<out RES : Response> : Event {

    abstract fun response(): RES

    fun response(responseCode: ResponseCode): RES {
        val response = this.response()
        response.code = responseCode.code
        response.text = responseCode.text
        return response
    }

}