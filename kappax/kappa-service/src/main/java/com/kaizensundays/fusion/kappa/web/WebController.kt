package com.kaizensundays.fusion.kappa.web

import com.kaizensundays.fusion.kappa.core.api.Service

/**
 * Created: Sunday 7/14/2024, 3:36 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class WebController {

    fun render(services: Map<String, Service>): String {

        return """
        <tr>
            <td class="kappa-td">Process 1</td>
            <td class="kappa-td">1234</td>
            <td class="kappa-td">com.example.MainClass1</td>
        </tr>
        <tr>
            <td class="kappa-td">Process 2</td>
            <td class="kappa-td">5678</td>
            <td class="kappa-td">com.example.MainClass2</td>
        </tr>
        <tr>
            <td class="kappa-td">Process 3</td>
            <td class="kappa-td">9011</td>
            <td class="kappa-td">com.example.MainClass3</td>
        </tr>
        """.trimIndent()
    }

}