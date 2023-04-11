package com.noljanolja.server.consumer.model

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