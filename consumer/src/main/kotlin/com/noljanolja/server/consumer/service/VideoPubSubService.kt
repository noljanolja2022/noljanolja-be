package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.toConsumerVideo
import com.noljanolja.server.consumer.model.VideoProgress
import com.noljanolja.server.consumer.model.VideoProgressEvent
import com.noljanolja.server.consumer.rsocket.SocketRequester
import com.noljanolja.server.consumer.rsocket.UserVideoProgress
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.springframework.data.redis.core.*
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class VideoPubSubService(
    private val videoRedisTemplate: ReactiveRedisTemplate<String, VideoProgress>,
    private val userVideoProgressRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val reactiveMsgListenerContainer: ReactiveRedisMessageListenerContainer,
    private val coreApi: CoreApi,
    private val socketRequester: SocketRequester,
) {
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun saveProgress(userId: String, progress: VideoProgress) {
        // TODO Add validation of progress
        // TODO Check if progress is actually coming from mobile app or from calling api
        val videoDetails = coreApi.getVideoDetails(progress.videoId).toConsumerVideo()
        val progressPercentage = progress.durationMs.toDouble() / videoDetails.durationMs
        if (progressPercentage < 0 || progressPercentage > 1) return
        when (progress.event) {
            VideoProgressEvent.PLAY -> {
                videoRedisTemplate.opsForValue().setAndAwait(
                    getProgressKey(userId, progress.videoId),
                    progress
                )
                userVideoProgressRedisTemplate.opsForSet().addAndAwait(userId, progress.videoId)
            }

            VideoProgressEvent.PAUSE -> {
                videoRedisTemplate.opsForValue().setAndAwait(
                    "$userId-${progress.videoId}",
                    progress
                )
            }

            VideoProgressEvent.FINISH -> {
                videoRedisTemplate.opsForValue().deleteAndAwait(getProgressKey(userId, progress.videoId))
                userVideoProgressRedisTemplate.opsForSet().removeAndAwait(userId, progress.videoId)
            }
        }
        if (progress.event != VideoProgressEvent.PAUSE) {
            GlobalScope.launch {
                socketRequester.emitUserWatchVideo(
                    UserVideoProgress(
                        userId = userId,
                        videoId = progress.videoId,
                        progressPercentage = progressPercentage,
                    )
                )
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