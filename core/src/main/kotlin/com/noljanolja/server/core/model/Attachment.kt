package com.noljanolja.server.core.model

data class Attachment(
    val id: Long = 0,
    val messageId: Long,
    val type: String,
    val originalName: String,
    val name: String,
    val size: Long,
    val md5: String,
)