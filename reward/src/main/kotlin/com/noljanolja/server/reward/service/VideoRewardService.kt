package com.noljanolja.server.reward.service

import com.noljanolja.server.loyalty.service.LoyaltyService
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
    private val loyaltyService: LoyaltyService,
) {
    suspend fun handleRewardUser(
        progressPercentage: Double,
        userId: String,
        videoId: String,
    ) {
        // find config for video if existed else use config of default video else throw err
        val configForVideo = videoRewardConfigRepo.findByVideoIdForUpdate(videoId)?.takeIf { it.isActive }
            ?: return
        // find progress configs of the video
        val progressConfigsForVideo = videoRewardProgressConfigRepo.findAllByConfigId(configForVideo.id)
            .toList()
            .ifEmpty { return }
        // find all reward records of user for the video
        val rewardRecords = videoRewardRecordRepo.findAllByUserIdAndConfigIdAndVideoId(
            userId = userId,
            configId = configForVideo.id,
            videoId = videoId,
        ).toList()
        // count number of times user received reward for each progress
        val rewardMapCounts = rewardRecords.groupingBy { it.rewardProgress }.eachCount()
        val lastRewardRecord = rewardRecords.maxByOrNull { it.createdAt }
        // for each progress config of the video check
        var totalReceivedPoints = 0L
        progressConfigsForVideo.mapNotNull { progressConfig ->
            //check whether user has received reward at this progress
            //check if progressConfig is less than or equal to progressPercentage
            //check if user received all the reward for at this progress
            //check if budget is still available
            if ((lastRewardRecord == null || (lastRewardRecord.rewardProgress < progressPercentage)) &&
                progressConfig.progress <= progressPercentage &&
                (rewardMapCounts[progressConfig.progress]?.toDouble() ?: 0.0) < configForVideo.maxApplyTimes &&
                configForVideo.totalPoints?.let { it >= configForVideo.rewardedPoints + progressConfig.rewardPoint } != false
            ) {
                configForVideo.rewardedPoints += progressConfig.rewardPoint
                totalReceivedPoints += progressConfig.rewardPoint
                VideoRewardRecordModel(
                    userId = userId,
                    configId = configForVideo.id,
                    rewardProgress = progressConfig.progress,
                    videoId = videoId,
                )
            } else null
        }.takeIf { it.isNotEmpty() }?.let {
            loyaltyService.addTransaction(
                memberId = userId,
                point = totalReceivedPoints,
                reason = "Watch video",
            )
            videoRewardConfigRepo.save(configForVideo)
            videoRewardRecordRepo.saveAll(it).toList()
        }
    }

    suspend fun getUserVideosRewards(
        userId: String,
        videoIds: Set<String>,
    ): List<UserVideoRewardRecord> {
        if (videoIds.isEmpty() || userId.isBlank()) return emptyList()
        val configs = videoRewardConfigRepo.findAllByVideoIdInAndActiveIsTrue(videoIds).toList().toMutableList()
            .ifEmpty { return emptyList() }
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
                            if (record.configId == config.id &&
                                record.videoId == videoId &&
                                record.rewardProgress == progressConfig.progress
                            )
                                record.createdAt
                            else null
                        }
                        UserVideoRewardRecord.RewardProgress(
                            progress = progressConfig.progress,
                            point = progressConfig.rewardPoint,
                            claimedAts = claimedAts,
                            completed = claimedAts.size >= config.maxApplyTimes,
                        )
                    } else null
                }
                UserVideoRewardRecord(
                    videoId = videoId,
                    rewardProgresses = rewardProgresses,
                    completed = rewardProgresses.all { it.completed },
                    totalPoints = config.rewardedPoints,
                    earnedPoints = rewardProgresses.sumOf { it.point * it.claimedAts.size },
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

    suspend fun getVideoRewardConfig(
        videoId: String
    ): VideoRewardConfig? {
        return videoRewardConfigRepo.findByVideoId(videoId)?.apply {
            rewardProgresses = videoRewardProgressConfigRepo.findAllByConfigId(id).toList()
        }?.toVideoRewardConfig()
    }

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
                totalPoints = newConfig.totalPoints
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