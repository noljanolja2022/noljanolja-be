package com.noljanolja.server.admin.model

data class UploadInfo(
    val path: String,
    val size: Long,
    val md5: String
)