package com.kaizensundays.fusion.kappa.messages

import com.fasterxml.jackson.databind.DeserializationFeature
import com.kaizensundays.fusion.kappa.core.api.Event
import com.kaizensundays.fusion.kappa.core.api.JacksonSerializable
import com.kaizensundays.fusion.kappa.core.api.Ping
import com.kaizensundays.fusion.kappa.core.api.Apply
import com.kaizensundays.fusion.kappa.core.Deployments
import com.kaizensundays.fusion.kappa.core.api.Service
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created: Sunday 7/2/2023, 1:24 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class ObjectSerializationTest {

    private val converter = JacksonObjectConverter<Event>()

    private val deployments = Deployments()

    @Test
    fun ping() {

        val ping = Ping()

        val json = converter.fromObject(ping)

        val obj = converter.toObject(json, Event::class.java)

        assertTrue(obj is Ping)
    }

    private fun String.insertAfter(index: Int, s: String): String {
        val sb = StringBuilder(this)
        sb.insert(index + 1, s)
        return sb.toString()
    }

    @Test
    fun service() {

        converter.jackson
            .addMixIn(Service::class.java, JacksonSerializable::class.java)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        val service = Service("easy-box")

        var json = converter.fromAnyObject(service)

        json = json.insertAfter(json.length - 2, ""","extraProp": "value" """)

        assertTrue(json.matches(".*@class.*".toRegex()))

        val obj = converter.toObject(json, Service::class.java)

        assertEquals("easy-box", obj.name)
    }

    @Test
    fun apply() {

        val serviceMap = runBlocking { deployments.readAndValidateDeployment("/easybox.yaml") }

        val apply = Apply(serviceMap)

        val json = converter.fromObject(apply)

        val obj = converter.toObject(json)

        assertTrue(obj is Apply)

        assertEquals("easybox", obj.serviceMap["easybox"]?.name)
        assertEquals("com.kaizensundays.fusion.kappa:kappa-node:%%version%%:jar", obj.serviceMap["easybox"]?.artifact)
    }

}