package com.kaizensundays.fusion.kappa

import com.kaizensundays.fusion.kappa.event.Handler
import com.kaizensundays.fusion.kappa.event.Request
import com.kaizensundays.fusion.kappa.event.Response
import com.kaizensundays.fusion.kappa.event.ResponseCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created: Sunday 3/12/2023, 1:07 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
abstract class AbstractHandler<in REQ : Request<RES>, out RES : Response> : Handler<REQ, RES> {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    abstract fun doHandle(request: REQ): RES

    protected fun response(responseCode: ResponseCode, request: REQ): RES {
        val response = request.response()
        response.code = responseCode.code
        response.text = responseCode.text
        return response
    }

    override fun handle(request: REQ): RES {
        try {
            return doHandle(request)
        } catch (e: Exception) {
            logger.error(e.message, e)
            return response(ResponseCode.SystemError, request)
        }
    }

}