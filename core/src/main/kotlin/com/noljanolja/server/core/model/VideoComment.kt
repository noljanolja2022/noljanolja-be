package com.noljanolja.server.core.model

data class VideoComment(
    val id: Long,
    val comment: String,
    val commenter: VideoCommenter,
) {
    data class VideoCommenter(
        val name: String,
        val avatar: String,
    )
}