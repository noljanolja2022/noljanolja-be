package com.noljanolja.server.consumer.model

import java.io.InputStream

data class UploadInfo(
    val path: String,
    val size: Long,
    val md5: String,
    val contentType: String,
    val fileName: String,
)

data class ResourceInfo(
    val data: InputStream,
    val contentType: String,
    var fileName: String? = null
)