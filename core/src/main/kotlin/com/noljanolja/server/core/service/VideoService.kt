package com.noljanolja.server.core.service

import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.Video
import com.noljanolja.server.core.repo.media.*
import com.noljanolja.server.core.rest.request.CreateVideoRequest
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
@Transactional
class VideoService(
    private val videoRepo: VideoRepo,
    private val videoViewCountRepo: VideoViewCountRepo,
    private val channelRepo: ChannelRepo,
) {
    suspend fun getVideoDetails(
        id: String,
    ): Video {
        return videoRepo.findById(id)?.apply {
            channel = channelRepo.findById(channelId)!!
            viewCount = videoViewCountRepo.getTotalViewCount(id)
        }?.toVideo() ?: throw Error.VideoNotFound
    }

    suspend fun getVideos(
        page: Int,
        pageSize: Int,
        isHighlighted: Boolean? = null,
    ): Pair<List<Video>, Long> {
        val videos = videoRepo.findAllBy(
            isHighlighted = isHighlighted,
            offset = (page - 1) * pageSize,
            limit = pageSize,
        ).toList()
        val total = videoRepo.countAllBy(
            isHighlighted = isHighlighted,
        )
        val channels = channelRepo.findAllById(videos.map { it.channelId }.toSet()).toList()
        return Pair(videos.map { videoModel ->
            videoModel.channel = channels.find { it.id == videoModel.channelId }!!
            videoModel.viewCount = videoViewCountRepo.getTotalViewCount(videoModel.id)
            videoModel.toVideo()
        }, total)
    }

    suspend fun deleteVideo(
        id: String,
    ) {
        videoRepo.findById(id) ?: throw Error.VideoNotFound
        videoRepo.deleteById(id)
    }

    suspend fun upsertVideo(
        videoInfo: CreateVideoRequest,
    ): Video {
        val channel = channelRepo.save(
            (channelRepo.findById(videoInfo.channelId)
                ?: ChannelModel(videoInfo.channelId).apply {
                    this.isNewRecord = true
                }).apply {
                title = videoInfo.channelTitle
                thumbnail = videoInfo.channelThumbnail
            }
        )
        var isNewModel = false
        return videoRepo.save(
            ((videoRepo.findById(videoInfo.id) ?: VideoModel(
                _id = videoInfo.id,
            ).apply { isNewRecord = true; isNewModel = true }).apply {
                title = videoInfo.title
                url = videoInfo.url
                publishedAt = videoInfo.publishedAt
                thumbnail = videoInfo.thumbnail
                likeCount = videoInfo.likeCount
                commentCount = videoInfo.commentCount
                favoriteCount = videoInfo.favoriteCount
                channelId = videoInfo.channelId
                duration = videoInfo.duration
                durationMs = videoInfo.durationMs
                isHighlighted = videoInfo.isHighlighted
            })
        )
            .also {
                if (isNewModel) {
                    videoViewCountRepo.save(
                        VideoViewCountModel(
                            videoId = videoInfo.id,
                            viewCount = 0,
                        )
                    )
                }
            }
            .apply {
                this.channel = channel
                this.viewCount = videoViewCountRepo.getTotalViewCount(id)
            }.toVideo()
    }

    suspend fun viewVideo(
        videoId: String,
    ) {
        val viewCountModel = videoViewCountRepo.findNewestVideoById(videoId)
            ?.takeIf { it.createdAt == LocalDate.now() }
            ?: VideoViewCountModel(
                videoId = videoId,
                viewCount = 0,
            )
        viewCountModel.viewCount++
        videoViewCountRepo.save(viewCountModel)
    }

    suspend fun getTrendingVideos(
        days: Int = 1,
        limit: Int = 10,
    ): List<Video> {
        return videoViewCountRepo.findTopTrendingVideos(
            days = days,
            limit = limit,
        ).toList().map {
            it.viewCount = videoViewCountRepo.getTotalViewCount(it.id)
            it.toVideo()
        }
    }
}