package com.noljanolja.server.consumer.adapter.core

import com.noljanolja.server.consumer.model.Channel
import com.noljanolja.server.consumer.model.Video
import java.time.Instant
import java.time.LocalDate

data class CoreVideo(
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
    val comments: List<CoreVideoComment> = listOf(),
    val channel: CoreChannel,
    val category: Category,
) {

    data class Category(
        val id: String,
        val title: String,
    )
}

data class CoreChannel(
    val id: String,
    val title: String,
    val thumbnail: String,
)

data class CorePromotedVideoConfig(
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
    val video: CoreVideo
)

fun CoreChannel.toChannel() = Channel(
    id = id, title = title, thumbnail = thumbnail
)

fun CoreVideo.toConsumerVideo(
    rewardRecords: CoreUserVideoRewardRecord? = null,
) = Video(
    id = id,
    url = url,
    publishedAt = publishedAt,
    title = title,
    thumbnail = thumbnail,
    duration = duration,
    durationMs = durationMs,
    viewCount = viewCount,
    likeCount = likeCount,
    favoriteCount = favoriteCount,
    commentCount = commentCount,
    isHighlighted = isHighlighted,
    comments = comments.map { it.toConsumerVideoComment() },
    channel = Channel(
        id = channel.id,
        title = channel.title,
        thumbnail = channel.thumbnail,
    ),
    category = Video.Category(
        id = category.id,
        title = category.title,
    ),
    earnedPoints = rewardRecords?.earnedPoints ?: 0,
    totalPoints = rewardRecords?.totalPoints ?: 0,
    completed = rewardRecords?.completed ?: false,
    rewardProgresses = rewardRecords?.rewardProgresses.orEmpty().map {
        Video.RewardProgress(
            progressMs = it.progress.toLong() * this.durationMs,
            point = it.point,
            claimedAts = it.claimedAts,
            completed = it.completed
        )
    }
)