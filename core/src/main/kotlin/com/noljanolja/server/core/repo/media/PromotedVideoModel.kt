package com.noljanolja.server.core.repo.media

import com.noljanolja.server.core.model.PromotedVideoConfig
import com.noljanolja.server.core.model.Video
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.time.LocalDate

@Table("promoted_videos")
data class PromotedVideoModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("video_id")
    val videoId: String,

    @Column("start_date")
    val startDate: LocalDate,

    @Column("end_date")
    val endDate: LocalDate,

    @Column("auto_play")
    val autoPlay: Boolean,

    @Column("auto_like")
    val autoLike: Boolean,

    @Column("auto_subscribe")
    val autoSubscribe: Boolean,

    @Column("auto_comment")
    val autoComment: Boolean,

    @Column("interaction_delay")
    val interactionDelay: Int,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
)


fun PromotedVideoModel.toPromotedVideo(video: Video) = PromotedVideoConfig(
    id = id,
    startDate = startDate,
    endDate = endDate,
    autoSubscribe = autoSubscribe,
    autoPlay = autoPlay,
    autoComment = autoComment,
    autoLike = autoLike,
    interactionDelay = interactionDelay,
    video = video,
    createdAt = createdAt,
    updatedAt = updatedAt,
)