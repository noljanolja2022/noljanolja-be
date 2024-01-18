package com.noljanolja.server.admin.model

import java.time.Instant
import java.time.LocalDate

data class VideoCreationReq(
    val youtubeUrl: String,
    val isHighlighted: Boolean = false,
    val availableFrom: Instant? = null,
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
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?,
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

data class PromotedVideoConfig(
    val id: Long = 0,
    val startDate: LocalDate,
    val autoPlay: Boolean,
    val autoLike: Boolean,
    val autoSubscribe: Boolean,
    val autoComment: Boolean,
    val interactionDelay: Int,
    val endDate: LocalDate,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val video: Video
)

data class PromoteVideoRequest (
    val autoLike: Boolean,
    val autoPlay: Boolean,
    val autoSubscribe: Boolean,
    val autoComment: Boolean,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val interactionDelay: Int
)