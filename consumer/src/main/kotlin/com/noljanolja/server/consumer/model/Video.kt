package com.noljanolja.server.consumer.model

import java.time.Instant

data class Video(
    val id: String,
    val url: String,
    val publishedAt: Instant,
    val title: String,
    val thumbnail: String,
    val duration: String,
    val durationMs: Long,
    var currentProgressMs: Long? = null,
    val viewCount: Long,
    val likeCount: Long,
    val favoriteCount: Long,
    val commentCount: Long,
    val isHighlighted: Boolean,
    val comments: List<VideoComment> = listOf(),
    val channel: Channel,
    val category: Category,
    val completed: Boolean = false,
    val earnedPoints: Long = 0,
    val totalPoints: Long = 0,
    val rewardProgresses: List<RewardProgress> = listOf(),
) {
    data class Category(
        val id: String,
        val title: String,
    )

    data class RewardProgress(
        val progressMs: Long,
        val point: Long,
        val claimedAts: List<Instant>,
        val completed: Boolean,
    )
}

data class VideoProgress(
    val videoId: String,
    val event: VideoProgressEvent,
    val durationMs: Long = 0,
    val trackIntervalMs: Long = 10000,
)

enum class VideoProgressEvent {
    PLAY, PAUSE, FINISH
}

data class RateVideoRequest(
    val youtubeToken: String?,
    val action: RateVideoAction
)

data class ChannelSubscriptionRequest(
    val youtubeToken: String,
    val isSubscribing: Boolean
)

enum class RateVideoAction {
    like, dislike, none
}