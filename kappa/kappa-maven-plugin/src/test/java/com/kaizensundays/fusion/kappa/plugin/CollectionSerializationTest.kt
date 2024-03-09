package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.service.Node
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Created: Sunday 1/28/2024, 7:00 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
class CollectionSerializationTest {

    @Test
    fun convertList() {

        val converter = Json { prettyPrint = true }

        val nodes = listOf(
            Node("1", "10.0.0.1", 0),
            Node("3", "10.0.0.3", 1),
            Node("7", "10.0.0.7", 2),
        )

        val json = converter.encodeToString(ListSerializer(Node.serializer()), nodes)

        val objs = converter.decodeFromString(ListSerializer(Node.serializer()), json)

        assertEquals(nodes, objs)
    }

}