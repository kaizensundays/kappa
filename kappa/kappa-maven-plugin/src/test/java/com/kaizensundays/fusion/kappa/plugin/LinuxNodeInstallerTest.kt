package com.kaizensundays.fusion.kappa.plugin

import com.kaizensundays.fusion.kappa.Result
import com.kaizensundays.fusion.kappa.os.Linux
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.io.File
import kotlin.test.assertEquals

/**
 * Created: Sunday 6/11/2023, 1:00 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
@Suppress("LocalVariableName")
class LinuxNodeInstallerTest {

    private val artifactManager: ArtifactManager = mock()
    private val mojo: AbstractKappaMojo = mock()
    private val os: Linux = mock()

    private val installer = LinuxNodeInstaller(artifactManager, mojo, os)

    private fun readText(location: String): String {
        return String(javaClass.getResourceAsStream(location)?.readBytes() ?: ByteArray(0)).replace("\r", "")
    }

    @Test
    fun systemdUnitFileLocation() {

        assertEquals("/home/bart/.config/systemd/user/", installer.systemdUnitFileLocation("/home/bart"))
        assertEquals("/home/lisa/.config/systemd/user/", installer.systemdUnitFileLocation("/home/lisa"))
    }

    @Test
    fun createSystemdUnitFile() {

        val unitFile = "easybox.service"
        val unitFileLocation = "target/.linux/systemd/"

        installer.createSystemdUnitFile(
            unitFile, unitFileLocation,
            "EasyBox", "easybox.sh", ".kappa"
        )

        val content = File("$unitFileLocation$unitFile").readText()

        val expected = readText('/' + javaClass.simpleName + '-' + unitFile)

        assertEquals(expected, content)
    }

    @Test
    fun systemCtlUser() {

        val Ok = Result(0, "")

        val params = listOf(
            installer::serviceStatus to "unit1",
            installer::serviceEnable to "unit2",
            installer::serviceDisable to "unit3",
            installer::serviceStart to "unit4",
            installer::serviceStop to "unit5",
        )

        val commands = listOf(
            listOf("/bin/bash", "-c", "systemctl --user status unit1"),
            listOf("/bin/bash", "-c", "systemctl --user enable unit2"),
            listOf("/bin/bash", "-c", "systemctl --user disable unit3"),
            listOf("/bin/bash", "-c", "systemctl --user start unit4"),
            listOf("/bin/bash", "-c", "systemctl --user stop unit5"),
        )

        params.zip(commands).forEach { (param, cmd) ->

            whenever(os.execute(cmd, 10)).thenReturn(Ok)

            val (func, unit) = param

            val result = func(unit)
            assertEquals(Ok, result)

            verify(os).execute(cmd, 10)
        }

        verifyNoMoreInteractions(os)
    }

    @Test
    fun uninstall() {

        installer.env = mapOf("HOME" to "/home/bart")
        installer.nodeName = "node"

        val Ok = Result(0, "")

        whenever(os.execute(any(), any())).thenReturn(Ok)

        installer.uninstall()

        verify(os).execute(listOf("/bin/bash", "-c", "systemctl --user stop node"), 10)
        verify(os).execute(listOf("/bin/bash", "-c", "systemctl --user disable node"), 10)
        verifyNoMoreInteractions(os)
    }

}