package com.kaizensundays.fusion.kappa.core.api

/**
 * Created: Saturday 12/26/2020, 5:19 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
enum class ResponseCode(val code: Int, val text: String) {

    Ok(0, "Ok"),
    SystemError(1, "SystemError"),
    UnexpectedArgument(2, "UnexpectedArgument"),
    UnexpectedRequestType(3, "UnexpectedRequestType"),
    Timeout(4, "Timeout"),

}