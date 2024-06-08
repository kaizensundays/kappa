package com.kaizensundays.fusion.kappa

import com.github.dockerjava.api.model.Bind
import com.github.dockerjava.api.model.Volume
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName
import java.lang.Thread.sleep

/**
 * Created: Sunday 6/2/2024, 12:57 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Tag("container-test")
class KappletContainerTest {

    companion object {
        private const val SERVER_PORT = 7701

        private const val IMAGE = "localhost:32000/kappa:latest"

        private val container: GenericContainer<*> = GenericContainer<Nothing>(DockerImageName.parse(IMAGE))

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            container.withExposedPorts(SERVER_PORT)
                .withCreateContainerCmdModifier { cmd ->
                    cmd.hostConfig?.withBinds(Bind("/home/super/var/shared/m2", Volume("/opt/m2")))
                }
            container.start()
        }

        @JvmStatic
        @AfterAll
        fun afterClass() {
            sleep(3_000)
            container.stop()
        }

    }


    @Test
    fun test() {
        sleep(10_000)
    }

}