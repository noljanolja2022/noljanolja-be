package com.noljanolja.server.reward.service

import com.noljanolja.server.reward.exception.Error
import com.noljanolja.server.reward.model.UserVideoRewardRecord
import com.noljanolja.server.reward.model.VideoRewardConfig
import com.noljanolja.server.reward.repo.*
import com.noljanolja.server.reward.rest.request.UpsertVideoConfigRequest
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class VideoRewardService(
    private val videoRewardConfigRepo: VideoRewardConfigRepo,
    private val videoRewardRecordRepo: VideoRewardRecordRepo,
    private val videoRewardProgressConfigRepo: VideoRewardProgressConfigRepo,
) {
    suspend fun getUserVideosRewards(
        userId: String,
        videoIds: Set<String>,
    ): List<UserVideoRewardRecord> {
        if (videoIds.isEmpty() || userId.isBlank()) return emptyList()
        val configs = videoRewardConfigRepo.findAllByVideoIdInAndActiveIsTrue(videoIds).toList().toMutableList()
            .also { configs ->
                if (configs.size < videoIds.size) {
                    videoRewardConfigRepo.findByVideoId("")?.let { defaultConfig ->
                        configs.add(defaultConfig)
                    }
                }
            }.ifEmpty { return emptyList() }
        val userRewardRecords = videoRewardRecordRepo.findAllByUserIdAndConfigIdIn(
            userId = userId,
            configIds = configs.map { it.id },
        ).toList()
        val rewardProgressesConfigs = videoRewardProgressConfigRepo.findAllByConfigIdIn(configs.map { it.id }).toList()
        return videoIds.mapNotNull { videoId ->
            (configs.find { it.videoId == videoId } ?: configs.find { it.videoId.isEmpty() })?.let { config ->
                val rewardProgresses = rewardProgressesConfigs.mapNotNull { progressConfig ->
                    if (progressConfig.configId == config.id) {
                        val claimedAts = userRewardRecords.mapNotNull { record ->
                            if (record.configId == config.id && record.rewardProgress == progressConfig.progress)
                                record.createdAt
                            else null
                        }
                        UserVideoRewardRecord.RewardProgress(
                            progress = progressConfig.progress,
                            point = progressConfig.rewardPoint,
                            claimedAts = claimedAts,
                            completed = config.maxApplyTimes == null || claimedAts.size >= config.maxApplyTimes!!,
                        )
                    } else null
                }
                UserVideoRewardRecord(
                    videoId = videoId,
                    rewardProgresses = rewardProgresses,
                    completed = rewardProgresses.all { it.completed }
                )
            }
        }
    }

    suspend fun getVideosRewardConfigs(
        page: Int,
        pageSize: Int,
    ): Pair<List<VideoRewardConfig>, Long> {
        val configs = videoRewardConfigRepo.findAllBy(PageRequest.of(page - 1, pageSize)).toList()
        val rewardProgresses = videoRewardProgressConfigRepo.findAllByConfigIdIn(configs.map { it.id }).toList()
        return Pair(
            configs.map { config ->
                config.rewardProgresses = rewardProgresses.filter { it.configId == config.id }
                config.toVideoRewardConfig()
            },
            videoRewardConfigRepo.count()
        )
    }

    suspend fun getVideoRewardConfigs(
        configId: Long,
    ) =
        videoRewardConfigRepo.findById(configId)?.apply {
            rewardProgresses = videoRewardProgressConfigRepo.findAllByConfigId(this.id).toList()
        }?.toVideoRewardConfig() ?: throw Error.ConfigNotFound

    suspend fun deleteVideoConfig(
        configId: Long,
    ) = videoRewardConfigRepo.deleteById(configId)

    suspend fun upsertVideoRewardConfigs(
        newConfig: UpsertVideoConfigRequest,
    ): VideoRewardConfig {
        if (newConfig.rewardProgresses.isEmpty()) throw Error.InvalidVideoConfig
        val config = videoRewardConfigRepo.save(
            (videoRewardConfigRepo.findByVideoId(newConfig.videoId) ?: VideoRewardConfigModel()).apply {
                isActive = newConfig.isActive
                maxApplyTimes = newConfig.maxApplyTimes
                videoId = newConfig.videoId
            }
        )
        val progresses = newConfig.rewardProgresses.map {
            VideoRewardProgressConfigModel(
                progress = it.progress,
                rewardPoint = it.point,
                configId = config.id,
            )
        }.let {
            videoRewardProgressConfigRepo.deleteAllByConfigId(config.id)
            videoRewardProgressConfigRepo.saveAll(it).toList()
        }
        return config.apply {
            this.rewardProgresses = progresses
        }.toVideoRewardConfig()
    }
}