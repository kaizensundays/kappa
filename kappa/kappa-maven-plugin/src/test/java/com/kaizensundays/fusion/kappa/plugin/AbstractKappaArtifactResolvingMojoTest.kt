package com.kaizensundays.fusion.kappa.plugin

import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.repository.RepositoryPolicy
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Created: Saturday 2/11/2023, 11:37 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class AbstractKappaArtifactResolvingMojoTest {

    private val mojo = object : AbstractKappaArtifactResolvingMojo() {
        override fun doExecute() {
        }
    }

    @Test
    fun setPrivateField() {

        val session = DefaultRepositorySystemSession()

        val field = mojo.setPrivateField("updatePolicy", RepositoryPolicy.UPDATE_POLICY_ALWAYS, session)

        assertEquals(RepositoryPolicy.UPDATE_POLICY_ALWAYS, field.get(session))
    }

}