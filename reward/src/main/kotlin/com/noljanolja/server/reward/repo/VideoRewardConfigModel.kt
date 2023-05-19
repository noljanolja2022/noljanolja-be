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

    @Column("created_at")
    @CreatedDate
    val createdAt: Instant = Instant.now(),

    @Column("updated_at")
    @LastModifiedDate
    val updatedAt: Instant = Instant.now(),
) {
    @Transient
    var rewardProgresses: List<VideoRewardProgressConfigModel> = listOf()
}

fun VideoRewardConfigModel.toVideoRewardConfig() = VideoRewardConfig(
    id = id,
    videoId = videoId,
    isActive = isActive,
    maxApplyTimes = maxApplyTimes,
    rewardProgresses = rewardProgresses.map {
        VideoRewardConfig.RewardProgress(
            point = it.rewardPoint,
            progress = it.progress,
        )
    }
)