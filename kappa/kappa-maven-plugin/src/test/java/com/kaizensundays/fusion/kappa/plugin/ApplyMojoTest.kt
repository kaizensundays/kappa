package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Service
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

/**
 * Created: Monday 1/30/2023, 7:22 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Disabled
class ApplyMojoTest {

    private val mojo = ApplyMojo()

    @Test
    fun resolveArtifacts() {

        val expectedArtifactMap = sortedMapOf(
            "groupId:threeId:1.0.3" to "/path/to/groupId/threeId/1.0.3/threeId.jar",
            "groupId:elevenId:0.11.0" to "/path/to/groupId/elevenId/0.11.0/elevenId.jar"
        )

        val serviceMap = mapOf(
            "one" to null,
            "three" to "groupId:threeId:1.0.3",
            "seven" to " ",
            "eleven" to "groupId:elevenId:0.11.0"
        )
            .map { (name, artifact) -> name to Service(name, artifact) }.toMap()

        val artifactMap = mojo.resolveArtifacts(serviceMap) { artifact -> expectedArtifactMap[artifact] ?: "?" }

        assertEquals(expectedArtifactMap, artifactMap.toSortedMap())

        assertThrows<IllegalStateException> { mojo.resolveArtifacts(serviceMap) { throw IllegalStateException() } }
    }

}