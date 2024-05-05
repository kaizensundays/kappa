package com.kaizensundays.fusion.kappa.core.api

/**
 * Created: Saturday 12/26/2020, 4:42 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
open class Response : Event {

    var code = 0
    var text = "Ok"

    override fun toString(): String {
        return javaClass.simpleName + "(code=$code, text='$text')"
    }

}