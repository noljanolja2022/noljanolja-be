package com.noljanolja.server.consumer.rest.request

import kotlinx.coroutines.flow.Flow
import org.springframework.core.io.buffer.DataBuffer

data class FileAttachment(
    val filename: String,
    val contentType: String?,
    val contentLength: Long,
    val data: Flow<DataBuffer>,
)
