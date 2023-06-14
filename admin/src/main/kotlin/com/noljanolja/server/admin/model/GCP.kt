package com.noljanolja.server.admin.model

import java.io.InputStream

data class UploadInfo(
    val path: String,
    val size: Long,
    val md5: String
)

data class ResourceInfo(
    val data : InputStream,
    val contentType: String,
    var fileName: String? = null
)