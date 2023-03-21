package com.noljanolja.server.common.utils

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
Take a zipped file and unzip it to a specific folder (will use current directory if not provided)
 */
fun File.extractZippedFile(dest: String? = null) : File {
    var basePath = absoluteFile.parentFile.path
    if (dest?.isNotEmpty() == true) {
        basePath += File.separator + dest
    }
    val dir = Paths.get(basePath + File.separator + nameWithoutExtension).toFile()
    if (!dir.exists())
        dir.mkdirs()
    val buffer = ByteArray(1024)
    try {
        val fis = FileInputStream(path)
        val zis = ZipInputStream(fis)
        var ze: ZipEntry? = zis.nextEntry
        while (ze != null) {
            val fileName: String = ze.name
            val newFile = File(dir.absolutePath + File.separator + fileName)
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

fun File.readJsonFileToString(): String {
    return String(Files.readAllBytes(Paths.get(absolutePath)))
}