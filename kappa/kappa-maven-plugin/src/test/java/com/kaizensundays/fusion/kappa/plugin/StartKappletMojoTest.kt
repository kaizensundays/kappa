package com.kaizensundays.fusion.kappa.plugin

import org.apache.maven.AbstractCoreMavenComponentTestSupport
import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.factory.ArtifactFactory
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StartKappletMojoTest : AbstractCoreMavenComponentTestSupport() {

    private lateinit var artifactFactory: ArtifactFactory

    private var localRepoDir = ""

    private lateinit var localRepo: ArtifactRepository

    @BeforeEach
    override fun before() {
        super.before()

        val mavenHome = getMavenHome()
        assertTrue(mavenHome != null && mavenHome.isNotBlank())

        localRepoDir = getLocalRepoDir(mavenHome)
        println("localRepoDir=$localRepoDir")
        assertTrue(File(localRepoDir).exists())

        artifactFactory = lookup(ArtifactFactory::class.java)
        localRepo = repositorySystem.createLocalRepository(File(localRepoDir))
    }

    private fun getMavenHome() = System.getenv("M2_HOME")

    private fun mavenLocalRepository(): String {
        val dir = System.getenv("M2_LOCAL_REPOSITORY")
        return if (dir != null && dir.isNotBlank() && File(dir).isDirectory) dir else ""
    }

    private fun String.toOsPath() = if (isWindows()) this.replace('/', '\\') else this.replace('\\', '/')

    private fun getDefaultLocalRepoDir(): String {
        val slash = System.getProperty("file.separator")
        val propName = if (isWindows()) "USERPROFILE" else "HOME"
        val userHome = System.getenv(propName)
        return "${userHome}${slash}.m2${slash}repository"
    }

    private fun getLocalRepoDir(settingsFile: File): String {
        val reader = InputStreamReader(FileInputStream(settingsFile), "UTF-8")
        val settings = SettingsXpp3Reader().read(reader)
        val repoDir = settings.localRepository
        return if (repoDir != null && repoDir.isNotBlank()) repoDir.toOsPath() else getDefaultLocalRepoDir()
    }

    private fun getLocalRepoDir(mavenHome: String): String {
        val localRepoDir = mavenLocalRepository()
        return if (localRepoDir.isNotBlank()) {
            localRepoDir
        } else {
            val settingsFile = File("${mavenHome}/conf/settings.xml")
            if (settingsFile.exists()) {
                getLocalRepoDir(settingsFile)
            } else {
                getDefaultLocalRepoDir()
            }
        }
    }

    override fun getProjectsDirectory(): String {
        return "?"
    }

    private fun createArtifact(groupId: String, artifactId: String, version: String): Artifact {

        val artifact = artifactFactory.createArtifactWithClassifier(groupId, artifactId, version, "jar", null)

        artifact.file = File(localRepo.basedir, localRepo.pathOf(artifact))

        return artifact
    }

    private fun isWindows(props: Properties) = props.getProperty("os.name").startsWith("Windows")

    private fun isWindows() = isWindows(System.getProperties())

    private class ArtifactAttrs(var groupId: String, var artifactId: String, var version: String)

    @Test
    fun test() {

        val expected = arrayOf(
            """\javax\cache\cache-api\1.1.1\cache-api-1.1.1.jar""",
            """\io\ktor\ktor-server-cio-jvm\2.1.1\ktor-server-cio-jvm-2.1.1.jar""",
            """\com\fasterxml\jackson\core\jackson-databind\2.12.6\jackson-databind-2.12.6.jar""",
        ).map { path -> (localRepoDir + path).toOsPath() }

        val artifactAttrs = arrayOf(
            ArtifactAttrs("javax.cache", "cache-api", "1.1.1"),
            ArtifactAttrs("io.ktor", "ktor-server-cio-jvm", "2.1.1"),
            ArtifactAttrs("com.fasterxml.jackson.core", "jackson-databind", "2.12.6"),
        )

        expected.zip(artifactAttrs).forEach { (expectedPath, attrs) ->
            val artifact = createArtifact(attrs.groupId, attrs.artifactId, attrs.version)
            assertEquals(expectedPath, artifact.file.canonicalPath)
        }
    }

}