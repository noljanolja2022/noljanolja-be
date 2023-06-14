package com.noljanolja.server.common.utils

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.codec.multipart.FilePart
import java.io.File
import java.nio.ByteBuffer

suspend fun FilePart.toByteBuffer() : ByteBuffer {
    return content().asFlow().map {
        val buffer = ByteBuffer.allocate(it.capacity())
        it.toByteBuffer(buffer)
        buffer
    }.first()
}

suspend fun FilePart.saveFileToLocal() : File {
    val localFile = File(filename())
    transferTo(localFile).awaitFirstOrNull()
    return localFile
}