package com.noljanolja.server.core.model

import java.time.Instant

data class VideoComment(
    val id: Long,
    val comment: String,
    val commenter: VideoCommenter,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    data class VideoCommenter(
        val name: String,
        val avatar: String,
    )
}