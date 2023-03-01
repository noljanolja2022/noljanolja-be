package com.noljanolja.server.core.model

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer
import java.time.Instant

data class Attachment(
    val id: Long = 0,
    val type: String,
    val originalName: String,
    val name: String,
    val size: Long,
    val md5: String,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
)

data class Attachments(
    val files: List<FileAttachment> = listOf()
)

data class FileAttachment(
    val filename: String,
    val contentType: String?,
    val contentLength: Long,
    val data: Flow<DataBuffer>,
)