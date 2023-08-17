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