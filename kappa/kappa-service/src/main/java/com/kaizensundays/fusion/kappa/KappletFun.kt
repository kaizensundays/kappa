package com.kaizensundays.fusion.kappa

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream
import java.io.*


/**
 * Created: Saturday 11/26/2022, 12:21 PM Eastern Time
 *
 * @author Sergey Chuykov
 */

fun isWindows() = System.getProperty("os.name").startsWith("Windows")

fun unsupportedOperation(): Nothing = throw UnsupportedOperationException()

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
