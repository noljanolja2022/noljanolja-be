package com.noljanolja.server.core.service

import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.Video
import com.noljanolja.server.core.repo.media.*
import com.noljanolja.server.core.rest.request.CreateVideoRequest
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class VideoService(
    private val videoRepo: VideoRepo,
    private val channelRepo: ChannelRepo,
) {
    suspend fun getVideoDetails(
        id: String,
    ): Video {
        return videoRepo.findById(id)?.apply {
            channel = channelRepo.findById(channelId)!!
        }?.toVideo() ?: throw Error.VideoNotFound
    }

    suspend fun getVideos(
        page: Int,
        pageSize: Int,
    ): List<Video> {
        val videos = videoRepo.findAllBy(PageRequest.of(page - 1, pageSize)).toList()
        val channels = channelRepo.findAllById(videos.map { it.channelId }.toSet()).toList()
        return videos.map { videoModel ->
            videoModel.channel = channels.find { it.id == videoModel.channelId }!!
            videoModel.toVideo()
        }
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
        return videoRepo.save(
            ((videoRepo.findById(videoInfo.id) ?: VideoModel(
                _id = videoInfo.id,
            ).apply { isNewRecord = true }).apply {
                title = videoInfo.title
                url = videoInfo.url
                publishedAt = videoInfo.publishedAt
                thumbnail = videoInfo.thumbnail
                likeCount = videoInfo.likeCount
                viewCount = videoInfo.viewCount
                commentCount = videoInfo.commentCount
                favoriteCount = videoInfo.favoriteCount
                channelId = videoInfo.channelId
                duration = videoInfo.duration
                durationMs = videoInfo.durationMs
            })
        ).apply {
            this.channel = channel
        }.toVideo()
    }
}