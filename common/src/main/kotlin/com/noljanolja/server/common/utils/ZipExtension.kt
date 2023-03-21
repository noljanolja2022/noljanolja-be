package com.noljanolja.server.common.utils

import java.io.ByteArrayInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun ZipOutputStream.addFileToZipStream(data: ByteArray, fileName: String) {
    val fileInputStream = ByteArrayInputStream(data)
    val zipEntry = ZipEntry(fileName)
    putNextEntry(zipEntry)

    val bytes = ByteArray(1024)
    var length: Int
    while (fileInputStream.read(bytes).also { length = it } >= 0) {
        write(bytes, 0, length)
    }
    fileInputStream.close()
}