package com.noljanolja.server.consumer.utils

import com.noljanolja.server.consumer.rest.request.FileAttachment
import kotlinx.coroutines.reactive.asFlow
import org.springframework.http.codec.multipart.FilePart

fun FilePart.toFileAttachment() = FileAttachment(
    filename = filename(),
    contentType = headers().contentType?.toString(),
    data = content().asFlow(),
    contentLength = headers().contentLength
)

fun getStickerPath(
    packId: Long, stickerName: String
) = "stickers/$packId/$stickerName"

val URL_REGEX = Regex("(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)")

fun String.extractLinks(): List<String> {
    return URL_REGEX.findAll(this).mapTo(mutableListOf()) { it.value }.distinct().map {
        if (!it.startsWith("http://") || !it.startsWith("https://")) {
            "http://${it}"
        } else it
    }
}