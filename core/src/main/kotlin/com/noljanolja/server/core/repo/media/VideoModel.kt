package com.noljanolja.server.core.repo.media

import com.noljanolja.server.core.model.Video
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

    @Column("view_count")
    var viewCount: Long = 0,

    @Column("like_count")
    var likeCount: Long = 0,

    @Column("favorite_count")
    var favoriteCount: Long = 0,

    @Column("comment_count")
    var commentCount: Long = 0,

    @Column("channel_id")
    var channelId: String = "",

    @Column("is_highlighted")
    var isHighlighted: Boolean = false,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) : Persistable<String> {
    @Transient
    var isNewRecord: Boolean = false

    override fun getId() = _id

    override fun isNew() = isNewRecord

    @Transient
    var channel: ChannelModel = ChannelModel()
}

fun VideoModel.toVideo() = Video(
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
    channelId = channelId,
    channelTitle = channel.title,
    channelThumbnail = channel.thumbnail,
)

