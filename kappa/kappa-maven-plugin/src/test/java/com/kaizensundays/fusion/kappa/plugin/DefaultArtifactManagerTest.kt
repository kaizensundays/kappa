package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.service.Service
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.repository.RepositoryPolicy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

/**
 * Created: Saturday 2/11/2023, 11:37 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class DefaultArtifactManagerTest {

    private val manager = DefaultArtifactManager(mock(), mock(), mock(), mock(), mock(), mock())

    @Test
    fun setPrivateField() {

        val session = DefaultRepositorySystemSession()

        val field = manager.setPrivateField("updatePolicy", RepositoryPolicy.UPDATE_POLICY_ALWAYS, session)

        assertEquals(RepositoryPolicy.UPDATE_POLICY_ALWAYS, field.get(session))
    }

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
            .map { (name, artifact) -> name to Service(name, artifact = artifact) }.toMap()

        val artifactMap = manager.resolveArtifacts(serviceMap) { artifact -> expectedArtifactMap[artifact] ?: "?" }

        assertEquals(expectedArtifactMap, artifactMap.toSortedMap())

        assertThrows<IllegalStateException> { manager.resolveArtifacts(serviceMap) { throw IllegalStateException() } }
    }

}