package com.kaizensundays.fusion.kappa.event

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.util.*

/**
 * Created: Friday 12/25/2020, 3:40 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class JacksonObjectConverter<T : JacksonSerializable>(
    prettyPrintEnabled: Boolean = false
) : ObjectConverter<T, String> {

    val jackson = ObjectMapper().registerKotlinModule()

    init {
        if (prettyPrintEnabled) {
            jackson.enable(SerializationFeature.INDENT_OUTPUT)
        }
        jackson.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        jackson.dateFormat = StdDateFormat()
            .withTimeZone(TimeZone.getTimeZone("UTC"))
            .withLocale(Locale.US)
            .withColonInTimeZone(true)
    }

    override fun fromObject(obj: T): String {
        return jackson.writeValueAsString(obj)
    }

    fun fromAnyObject(obj: Any): String {
        return jackson.writeValueAsString(obj)
    }

    fun fromObjects(objs: Any): String {
        return jackson.writerWithDefaultPrettyPrinter().writeValueAsString(objs)
    }

    fun <O> toObject(wire: String, type: Class<O>): O {
        return jackson.readValue(wire, type)
    }

    override fun toObject(wire: String): T {
        @Suppress("UNCHECKED_CAST")
        return toObject(wire, JacksonSerializable::class.java) as T
    }

    fun <O> toObjects(wire: String, typeRef: TypeReference<O>): O {
        return jackson.readValue(wire, typeRef)
    }

}