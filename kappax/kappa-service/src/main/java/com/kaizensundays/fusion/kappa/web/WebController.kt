package com.kaizensundays.fusion.kappa.web

import com.kaizensundays.fusion.kappa.service.Kapplet
import kotlinx.html.TABLE
import kotlinx.html.stream.createHTML
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr

/**
 * Created: Sunday 7/14/2024, 3:36 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class WebController(
    private val kapplet: Kapplet
) {

    private val kappaTHead = "kappa-thead"
    private val kappaTh = "kappa-th"
    private val kappaTd = "kappa-td"

    private inline fun renderServices(crossinline tbody: TABLE.() -> Unit): String {
        return createHTML().table("kappa-service-table") {
            thead(kappaTHead) {
                tr {
                    th(classes = kappaTh) { +"Name" }
                    th(classes = kappaTh) { +"PID" }
                    th(classes = kappaTh) { +"Main Class" }
                }
            }
            tbody.invoke(this)
        }
    }

    fun renderServices(): String {
        return renderServices {
            tbody {
                tr {
                    td(classes = kappaTd) { +"Process 1" }
                    td(classes = kappaTd) { +"123" }
                    td(classes = kappaTd) { +"com.example.MainClass1" }
                }
                tr {
                    td(classes = kappaTd) { +"Process 2" }
                    td(classes = kappaTd) { +"456" }
                    td(classes = kappaTd) { +"com.example.MainClass2" }
                }
                tr {
                    td(classes = kappaTd) { +"Process 1" }
                    td(classes = kappaTd) { +"789" }
                    td(classes = kappaTd) { +"com.example.MainClass3" }
                }
            }
        }
    }

    fun clearServices(): String {
        return renderServices {}
    }

}