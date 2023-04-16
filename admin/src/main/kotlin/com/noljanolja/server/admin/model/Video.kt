package com.noljanolja.server.admin.model

import java.time.Instant

data class VideoCreationReq(
    val youtubeUrl: String,
    val isHighlighted: Boolean = false
)



data class Video(
    val id: String,
    val url: String,
    val publishedAt: Instant,
    val title: String,
    val thumbnail: String,
    val duration: String,
    val durationMs: Long,
    val viewCount: Long,
    val likeCount: Long,
    val favoriteCount: Long,
    val commentCount: Long,
    val isHighlighted: Boolean,
    val comments: List<VideoComment> = listOf(),
    val channel: Channel,
    val category: Category,
) {
    data class Channel(
        val id: String,
        val title: String,
        val thumbnail: String,
    )

    data class Category(
        val id: String,
        val title: String,
    )
}

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