package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.model.VideoProgress
import com.noljanolja.server.consumer.model.VideoProgressEvent
import kotlinx.coroutines.flow.Flow
import org.springframework.data.redis.core.*
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Service

@Service
class VideoPubSubService(
    private val videoRedisTemplate: ReactiveRedisTemplate<String, VideoProgress>,
    private val userVideoProgressRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val reactiveMsgListenerContainer: ReactiveRedisMessageListenerContainer
) {

    suspend fun saveProgress(userId: String, progress: VideoProgress) {
        when (progress.event) {
            VideoProgressEvent.PLAY -> {
                videoRedisTemplate.opsForValue().setAndAwait(
                    getProgressKey(userId, progress.videoId),
                    progress
                )
//                val currentProgress =
//                    videoRedisTemplate.opsForValue().getAndAwait(getProgressKey(userId, progress.videoId))
//                if (currentProgress != null) {
//                    if (progress.durationMs - currentProgress.durationMs > progress.trackIntervalMs) {
//                        videoRedisTemplate.opsForValue().setAndAwait(
//                            getProgressKey(userId, progress.videoId),
//                            progress
//                        )
//                    }
//                } else {
//
//                }
                if (progress.durationMs == 0L) {
                    userVideoProgressRedisTemplate.opsForSet().addAndAwait(userId, progress.videoId)
                }
            }

            VideoProgressEvent.PAUSE -> {
                videoRedisTemplate.opsForValue().setAndAwait(
                    "$userId-${progress.videoId}",
                    progress
                )
            }

            VideoProgressEvent.FINISH -> {
//                videoRedisTemplate.opsForValue().getAndAwait(getProgressKey(userId, progress.videoId))
                videoRedisTemplate.opsForValue().deleteAndAwait(getProgressKey(userId, progress.videoId))
                userVideoProgressRedisTemplate.opsForSet().removeAndAwait(userId, progress.videoId)
            }
        }
    }

    suspend fun getWatchingVideos(userId: String): Flow<String> {
        return userVideoProgressRedisTemplate.opsForSet().membersAsFlow(userId)
    }

    suspend fun getWatchingProgress(userId: String, videoIds: List<String>): List<VideoProgress> {
        return videoRedisTemplate.opsForValue().multiGetAndAwait(
            videoIds.map { getProgressKey(userId, it) }
        ).filterNotNull()
    }

    private fun getProgressKey(userId: String, videoId: String) = "$userId-$videoId"
}