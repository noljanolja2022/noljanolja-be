package com.noljanolja.server.reward.repo

import com.noljanolja.server.reward.model.VideoRewardConfig
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("video_reward_configs")
data class VideoRewardConfigModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("video_id")
    var videoId: String = "",

    @Column("active")
    var isActive: Boolean = false,

    @Column("max_apply_times")
    var maxApplyTimes: Int = 0,

    @Column("total_points")
    var totalPoints: Long? = null,

    @Column("rewarded_points")
    var rewardedPoints: Long = 0,

    @Column("comment_reward_points")
    var commentRewardPoints: Long = 0,

    @Column("min_comment_length")
    var minCommentLength: Int = 0,

    @Column("comment_max_apply_times")
    var commentMaxApplyTimes: Int = 0,

    @Column("comment_total_applied_times")
    var commentTotalAppliedTimes: Int = 0,

    @Column("like_reward_points")
    var likeRewardPoints: Long = 0,

    @Column("like_max_apply_times")
    var likeMaxApplyTimes: Int = 0,

    @Column("like_total_applied_times")
    var likeTotalAppliedTimes: Int = 0,

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),

    @Column("accumulation_config_log")
    val accumulationConfigLog: String? = null
) {
    @Transient
    var rewardProgresses: List<VideoRewardProgressConfigModel> = listOf()
}

fun VideoRewardConfigModel.toVideoRewardConfig() = VideoRewardConfig(
    id = id,
    videoId = videoId,
    isActive = isActive,
    maxApplyTimes = maxApplyTimes,
    totalPoints = totalPoints,
    rewardProgresses = rewardProgresses.map {
        VideoRewardConfig.RewardProgress(
            point = it.rewardPoint,
            progress = it.progress,
        )
    },
    commentMaxApplyTimes = commentMaxApplyTimes,
    commentRewardPoints = commentRewardPoints,
    minCommentLength = minCommentLength,
    likeMaxApplyTimes = likeMaxApplyTimes,
    likeRewardPoints = likeRewardPoints,
)