package com.kaizensundays.fusion.kappa.messages

import com.kaizensundays.fusion.kappa.service.Apply
import com.kaizensundays.fusion.kappa.service.Deployments
import com.kaizensundays.fusion.kappa.event.Event
import com.kaizensundays.fusion.kappa.event.JacksonObjectConverter
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

    @Test
    fun apply() {

        val serviceMap = runBlocking { deployments.readAndValidateDeployment("/easybox.yaml") }

        val apply = Apply("", emptyMap(), serviceMap)

        val json = converter.fromObject(apply)

        val obj = converter.toObject(json)

        assertTrue(obj is Apply)

        assertEquals("easybox", obj.serviceMap["easybox"]?.name)
        assertEquals("com.kaizensundays.fusion:kappa-node:%%version%%:jar", obj.serviceMap["easybox"]?.artifact)
    }

}