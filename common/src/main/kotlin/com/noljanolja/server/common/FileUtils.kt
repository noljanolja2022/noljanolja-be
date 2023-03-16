package com.noljanolja.server.common

import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.codec.multipart.FilePart
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object FileUtils {
    suspend fun saveFileToLocal(zippedFile: FilePart) : File {
        val localFile = File(zippedFile.filename())
        zippedFile.transferTo(localFile).awaitFirstOrNull()
        return localFile
    }

    suspend fun processFileAsByteArray(files: List<File>, callBack: suspend (File, ByteBuffer) -> Unit) {
        if (files.isEmpty()) {
            return
        }
        try {
            files.forEach {
                val bytes = Files.readAllBytes(Paths.get(it.absolutePath))
                callBack.invoke(it, ByteBuffer.wrap(bytes))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }
    }

    fun extractZippedFile(file: File, extractFolder: String? = null) : File {
        var basePath = file.absoluteFile.parentFile.path
        if (extractFolder?.isNotEmpty() == true) {
            basePath += File.separator + extractFolder
        }
        val dir = Paths.get(basePath + File.separator + file.nameWithoutExtension).toFile()
        if (!dir.exists())
            dir.mkdirs()
        val buffer = ByteArray(1024)
        try {
            val fis = FileInputStream(file.path)
            val zis = ZipInputStream(fis)
            var ze: ZipEntry? = zis.nextEntry
            while (ze != null) {
                val fileName: String = ze.name
                val newFile = File(dir.absolutePath + File.separator + fileName)
                println("Unzipping to " + newFile.absolutePath)
                //create directories for sub directories in zip
                File(newFile.parent).mkdirs()
                val fos = FileOutputStream(newFile)
                var len: Int
                while (zis.read(buffer).also { len = it } > 0) {
                    fos.write(buffer, 0, len)
                }
                fos.close()
                //close this ZipEntry
                zis.closeEntry()
                ze = zis.nextEntry
            }
            //close last ZipEntry
            zis.closeEntry()
            zis.close()
            fis.close()
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }
        return dir
    }

    fun readFileToString(jsonFile: File): String {
        return String(Files.readAllBytes(Paths.get(jsonFile.absolutePath)))
    }
}