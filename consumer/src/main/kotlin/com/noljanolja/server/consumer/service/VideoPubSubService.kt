package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.toConsumerVideo
import com.noljanolja.server.consumer.model.VideoProgress
import com.noljanolja.server.consumer.model.VideoProgressEvent
import com.noljanolja.server.consumer.model.VideoWatchRecordDetail
import com.noljanolja.server.consumer.rsocket.SocketRequester
import com.noljanolja.server.consumer.rsocket.UserVideoProgress
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.springframework.data.redis.core.*
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class VideoPubSubService(
    private val videoRedisTemplate: ReactiveRedisTemplate<String, VideoWatchRecordDetail>,
    private val userVideoProgressRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val reactiveMsgListenerContainer: ReactiveRedisMessageListenerContainer,
    private val coreApi: CoreApi,
    private val socketRequester: SocketRequester,
) {

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun updateWatchProgress(userId: String, payload: VideoProgress) {
        val videoId = payload.videoId
        val videoDetail = coreApi.getVideoDetail(videoId).toConsumerVideo()
        val totalVideoTime = videoDetail.durationMs
        val cachedRecord = videoRedisTemplate.opsForValue().getAndAwait(getProgressKey(userId, videoId))
        val now = Instant.now()
        println("------------------------------------")
        // If this is watching old video
        if (cachedRecord != null) {
            println(
                "cacheRecord:" +
                " videoId: ${cachedRecord.videoId}," +
                " lastAction: ${cachedRecord.lastAction}, " +
                " lastServerTime: ${cachedRecord.lastServerTime}, " +
                " durationMs: ${cachedRecord.durationMs}, " +
                " accumulatedTimeMs: ${cachedRecord.accumulatedTimeMs}"
            )

            println(
                "payload:" +
                " videoId: ${payload.videoId}," +
                " event: ${payload.event}, " +
                " durationMs: ${payload.durationMs}, " +
                " trackIntervalMs: ${payload.trackIntervalMs}"
            )
            val serverElapsedTime = now.epochSecond - cachedRecord.lastServerTime
            val userElapsedTime = (payload.durationMs - cachedRecord.durationMs) / 1000
            if (payload.event == VideoProgressEvent.PLAY) {
                if (cachedRecord.lastAction == VideoProgressEvent.PLAY) {
                    if (payload.durationMs < cachedRecord.durationMs) {
                        println("$userId is rewinding time for video $videoId. Progress ignored")
                    } else if (userElapsedTime > serverElapsedTime + 1) {
                        println("$userId is fast forwarding for video $videoId. Progress ignored")
                    } else {
                        cachedRecord.accumulatedTimeMs =
                            cachedRecord.accumulatedTimeMs + payload.durationMs - cachedRecord.durationMs
                        val newProgressPercentage = cachedRecord.accumulatedTimeMs.toDouble() / totalVideoTime
                        GlobalScope.launch {
                            socketRequester.emitUserWatchVideo(
                                UserVideoProgress(
                                    userId = userId,
                                    videoId = payload.videoId,
                                    progressPercentage = newProgressPercentage,
                                )
                            )
                        }
                        if (cachedRecord.accumulatedTimeMs >= totalVideoTime) {
                            println("$userId watched $videoId to the end. Cleaning up redis")
                            videoRedisTemplate.opsForValue().deleteAndAwait(getProgressKey(userId, videoId))
                            userVideoProgressRedisTemplate.opsForSet().removeAndAwait(userId, videoId)
                            return
                        }
                    }
                }
            } else if (payload.event == VideoProgressEvent.PAUSE && cachedRecord.lastAction == VideoProgressEvent.PLAY) {
                if (payload.durationMs < cachedRecord.durationMs + 1) {
                    cachedRecord.accumulatedTimeMs =
                        cachedRecord.accumulatedTimeMs + payload.durationMs - cachedRecord.durationMs
                }
            }
            cachedRecord.durationMs = payload.durationMs
            cachedRecord.lastServerTime = now.epochSecond
            videoRedisTemplate.opsForValue().setAndAwait(
                getProgressKey(userId, payload.videoId),
                cachedRecord
            )
            println(
                "Elapsed server: $serverElapsedTime, " +
                        "User elapsed time: $userElapsedTime, " +
                        "Accumulated: ${cachedRecord.accumulatedTimeMs} / ${videoDetail.durationMs}"
            )
        } else {
            println("$userId Start watching video $videoId")
            // if this is first time watching video
            if (payload.event == VideoProgressEvent.PLAY) {
                userVideoProgressRedisTemplate.opsForSet().addAndAwait(userId, payload.videoId)
                videoRedisTemplate.opsForValue().setAndAwait(
                    getProgressKey(userId, payload.videoId),
                    VideoWatchRecordDetail(
                        videoId = payload.videoId,
                        durationMs = payload.durationMs,
                        lastAction = payload.event,
                        lastServerTime = now.epochSecond
                    )
                )
            }
        }
    }

    suspend fun getWatchingVideos(userId: String): Flow<String> {
        return userVideoProgressRedisTemplate.opsForSet().membersAsFlow(userId)
    }

    suspend fun getWatchingProgress(userId: String, videoIds: List<String>): List<VideoWatchRecordDetail> {
        return videoRedisTemplate.opsForValue().multiGetAndAwait(
            videoIds.map { getProgressKey(userId, it) }
        ).filterNotNull()
    }

    private fun getProgressKey(userId: String, videoId: String) = "$userId-$videoId"

    suspend fun clearVideoProgress(userId: String, videoId: String) {
        videoRedisTemplate.opsForValue().deleteAndAwait(getProgressKey(userId, videoId))
    }
}