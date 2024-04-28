package com.kaizensundays.fusion.kappa.core.api

/**
 * Created: Saturday 10/16/2021, 1:46 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
fun interface Handler<in REQ : Request<RES>, out RES : Response> {

    fun handle(request: REQ): RES

}