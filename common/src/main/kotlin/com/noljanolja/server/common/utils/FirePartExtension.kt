package com.noljanolja.server.common.utils

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import java.io.ByteArrayOutputStream
import java.io.File

suspend fun FilePart.toBytes(): ByteArray {
    val bytesList: List<ByteArray> = this.content()
        .flatMap { dataBuffer -> Flux.just(dataBuffer.asByteBuffer().array()) }
        .collectList()
        .awaitFirst()

    // concat ByteArrays
    val byteStream = ByteArrayOutputStream()
    bytesList.forEach { bytes -> byteStream.write(bytes) }
    return byteStream.toByteArray()
}

suspend fun FilePart.saveFileToLocal() : File {
    val localFile = File(filename())
    transferTo(localFile).awaitFirstOrNull()
    return localFile
}