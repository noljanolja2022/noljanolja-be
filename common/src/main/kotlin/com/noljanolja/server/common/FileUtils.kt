package com.noljanolja.server.common

import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths


object FileUtils {
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
}