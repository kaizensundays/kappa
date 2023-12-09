package com.kaizensundays.fusion.kappa.service

import com.kaizensundays.fusion.kappa.createTarBz2
import com.kaizensundays.fusion.kappa.extractTarBz2
import com.kaizensundays.fusion.kappa.findFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertTrue

/**
 * Created: Saturday 2/4/2023, 11:31 AM Eastern Time
 *
 * @author Sergey Chuykov
 */
class KappletFunTest {

    @Test
    fun findFile() {

        val locations = listOf("pom.xml", "deployment.yaml", "/deployment.yaml")

        locations.forEach { location ->

            val file = findFile(location)

            assert(file.exists())
        }

        assertThrows<IllegalArgumentException> { findFile("no-such.yaml") }
    }

    @Test
    fun createAndExtractTarBz2() {

        val srcDir = "target/.kappa/bundles/"

        File(srcDir).deleteRecursively()

        val bundleFile = "bundle-1.0-SNAPSHOT.tar.bz2"

        val source = mapOf(
            "readme.txt" to "test",
            "bin/entrypoint.bat" to "echo Ok",
            "conf/server.xml" to "<root/>"
        ).map { entry -> srcDir + entry.key to entry.value }.toMap()

        source.entries.forEach { entry ->
            val file = File(entry.key)
            file.parentFile.mkdirs()
            file.writeText(entry.value)
        }

        createTarBz2(srcDir, bundleFile)

        assertTrue(File(srcDir + bundleFile).exists())

        val workingDir = "target/.kappa/work/bundle"

        File(workingDir).deleteRecursively()

        extractTarBz2(File(srcDir + bundleFile), File(workingDir))
    }

}