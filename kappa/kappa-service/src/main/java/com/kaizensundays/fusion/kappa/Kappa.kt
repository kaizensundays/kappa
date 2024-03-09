package com.kaizensundays.fusion.kappa

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import javax.cache.Cache

/**
 * Created: Sunday 11/20/2022, 12:05 PM Eastern Time
 *
 * @author Sergey Chuykov
 */
object Kappa {

    const val MOJO_CONFIGURATION = "mojo.properties"

    const val ENV_KAPPA_HOSTS = "KAPPA_HOSTS"

    const val PROP_KAPPA_DEFAULT_HOSTS = "kappa.default.hosts"

    const val NODE_SERVICE_NAME = "kapplet"

    const val serviceTagPrefix = "com.kaizensundays.kappa.service.id."

    const val baseDir = ".kappa"

    const val workDir = "work"

    const val MOJO_SLEEP_AFTER_SEC = 1L
    const val MOJO_SLEEP_AFTER_PROP = "sleep.after"

}

inline fun <reified T> Any.cast() = this as T

fun isWindows() = System.getProperty("os.name").startsWith("Windows")

fun unsupportedOperation(): Nothing = throw UnsupportedOperationException()

fun String.uppercaseFirstChar() = this.replaceFirst(this[0], this[0].uppercaseChar())

fun Any.getResourceAsInputStream(location: String): InputStream {

    val file = File(location)

    return if (file.exists()) {
        file.inputStream()
    } else {
        val name = if (location.startsWith('/')) location else "/$location"
        this.javaClass.getResourceAsStream(name) ?: throw IllegalArgumentException("File not found: $location")
    }
}

fun Any.findFile(location: String): File {

    val file = File(location)

    return if (file.exists()) {
        file
    } else {
        val name = if (location.startsWith('/')) location else "/$location"
        val url = this.javaClass.getResource(name)
        if (url != null) {
            File(url.toURI())
        } else {
            throw IllegalArgumentException("File not found: $location")
        }
    }
}

fun getFilesInDirectory(dir: File): List<String> {

    val files = mutableListOf<String>()

    dir.walkTopDown().forEach {
        if (it.isFile) {
            files.add(dir.toPath().relativize(it.toPath()).toString())
        }
    }
    return files
}

fun createTarBz2(dir: String, tar: String) {

    val archiveFileName = dir + tar
    val fileNames = getFilesInDirectory(File(dir))

    val tarFile = File(archiveFileName)
    val fos = FileOutputStream(tarFile)
    val bos = BufferedOutputStream(fos)
    val bzOut = BZip2CompressorOutputStream(bos)
    val tarOut = TarArchiveOutputStream(bzOut)

    for (fileName in fileNames) {
        val file = File(dir + fileName)
        val entry = TarArchiveEntry(file, fileName)
        tarOut.putArchiveEntry(entry)
        val bis = BufferedInputStream(FileInputStream(file))
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (bis.read(buffer).also { bytesRead = it } != -1) {
            tarOut.write(buffer, 0, bytesRead)
        }
        bis.close()
        tarOut.closeArchiveEntry()
    }

    tarOut.finish()
    tarOut.close()
    bzOut.close()
    bos.close()
    fos.close()
}

fun extractTarBz2(tarBz2File: File, destinationDir: File) {
    val buffer = ByteArray(1024)
    val tarBz2Stream = FileInputStream(tarBz2File)
    val bz2Stream = BZip2CompressorInputStream(tarBz2Stream)
    val tarStream = TarArchiveInputStream(bz2Stream)

    var entry = tarStream.nextTarEntry
    while (entry != null) {
        val outputFile = File(destinationDir, entry.name)
        if (entry.isDirectory) {
            outputFile.mkdirs()
        } else {
            outputFile.parentFile.mkdirs()
            val output = BufferedOutputStream(FileOutputStream(outputFile))
            var bytesRead = tarStream.read(buffer, 0, buffer.size)
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead)
                bytesRead = tarStream.read(buffer, 0, buffer.size)
            }
            output.close()
        }
        entry = tarStream.nextTarEntry
    }

    tarStream.close()
    bz2Stream.close()
    tarBz2Stream.close()
}

fun Properties.getInt(name: String, defaultValue: Int) = this.getProperty(name)?.toInt() ?: defaultValue

fun getPropertyAsInt(name: String, defaultValue: Int) = System.getProperties().getInt(name, defaultValue)

fun Properties.getLong(name: String, defaultValue: Long) = this.getProperty(name)?.toLong() ?: defaultValue

fun getPropertyAsLong(name: String, defaultValue: Long) = System.getProperties().getLong(name, defaultValue)

fun <K, V> Cache<K, V>.toMap(): Map<K, V> {
    return this.associate { e -> e.key to e.value }
}
