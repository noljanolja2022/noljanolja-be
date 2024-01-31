package com.noljanolja.server.core.repo.media

import com.noljanolja.server.core.model.TrackInfo
import com.noljanolja.server.core.model.Video
import com.noljanolja.server.core.model.VideoLogTransaction
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("videos")
data class VideoModel(
    @Id
    @Column("id")
    val _id: String,

    @Column("url")
    var url: String = "",

    @Column("published_at")
    var publishedAt: Instant = Instant.now(),

    @Column("title")
    var title: String = "",

    @Column("thumbnail")
    var thumbnail: String = "",

    @Column("duration")
    var duration: String = "",

    @Column("duration_ms")
    var durationMs: Long = 0,

    @Column("favorite_count")
    var favoriteCount: Long = 0,

    @Column("channel_id")
    var channelId: String = "",

    @Column("category_id")
    var categoryId: String = "",

    @Column("is_highlighted")
    var isHighlighted: Boolean = false,

    @Column("view_count")
    var viewCount: Long = 0,

    @Column("like_count")
    var likeCount: Long = 0,

    @Column("comment_count")
    var commentCount: Long = 0,

    @Column("available_from")
    var availableFrom: Instant? = null,

    @Column("available_to")
    var availableTo: Instant? = null,

    @Column("is_deactivated")
    var isDeactivated: Boolean = false,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),

    @Column("deleted_at")
    var deletedAt: Instant? = null,
) : Persistable<String> {
    @Transient
    var isNewRecord: Boolean = false

    override fun getId() = _id

    override fun isNew() = isNewRecord

    @Transient
    var channel: VideoChannelModel = VideoChannelModel()

    @Transient
    var comments: List<VideoCommentModel> = listOf()

    @Transient
    var category: VideoCategoryModel = VideoCategoryModel()
}

fun VideoModel.toVideo(isLiked: Boolean? = null) = Video(
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
    availableFrom = availableFrom,
    availableTo = availableTo,
    isDeactivated = isDeactivated,
    channel = channel.toVideoChannel(),
    category = category.toVideoCategory(),
    comments = comments.map { it.toVideoComment() },
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    isLiked = isLiked
)

fun VideoModel.toVideoLogTransaction(progressPercentage: Double) = VideoLogTransaction (
    id = id,
    url = url,
    title = title,
    thumbnail = thumbnail,
    progressPercentage = progressPercentage,
    durationMs = durationMs
)

fun VideoModel.toTrackInfo(rewardedPoints: Long) = TrackInfo(
    id = id,
    title = title,
    viewCount = viewCount,
    likeCount = likeCount,
    favoriteCount = favoriteCount,
    commentCount = commentCount,
    rewardedPoints = rewardedPoints
)