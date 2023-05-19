package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.request.LikeVideoRequest
import com.noljanolja.server.consumer.adapter.core.request.PostCommentRequest
import com.noljanolja.server.consumer.adapter.core.toConsumerVideo
import com.noljanolja.server.consumer.adapter.core.toConsumerVideoComment
import com.noljanolja.server.consumer.adapter.youtube.YoutubeApi
import com.noljanolja.server.consumer.model.StickerPack
import com.noljanolja.server.consumer.model.Video
import com.noljanolja.server.consumer.model.VideoComment
import com.noljanolja.server.consumer.model.VideoProgress
import org.springframework.stereotype.Component

@Component
class MediaService(
    private val coreApi: CoreApi,
    private val youtubeApi: YoutubeApi,
    private val videoPubSubService: VideoPubSubService,
) {
    suspend fun getAllStickerPacks(userId: String): List<StickerPack> {
        return coreApi.getAllStickerPacksFromUser(userId)!!
    }

    suspend fun getStickerPack(stickerPackId: Long): StickerPack {
        return coreApi.getStickerPack(stickerPackId)!!
    }

    suspend fun likeVideo(
        videoId: String,
        userId: String,
    ) {
        coreApi.likeVideo(
            videoId = videoId,
            payload = LikeVideoRequest(userId)
        )
    }

    suspend fun postComment(
        comment: String,
        userId: String,
        videoId: String,
        youtubeBearer: String? = null,
    ): VideoComment {
        if (youtubeBearer != null) {
            val youtubeRes = youtubeApi.addToplevelComment(videoId, youtubeBearer, comment)
            println("Posted comment success with id ${youtubeRes.id}")
        }
        return coreApi.postComment(
            videoId = videoId,
            payload = PostCommentRequest(
                commenterId = userId,
                comment = comment,
            )
        ).toConsumerVideoComment()
    }

    suspend fun getVideos(
        page: Int,
        pageSize: Int,
        isHighlighted: Boolean? = null,
        categoryId: String? = null,
        userId: String,
    ): Pair<List<Video>, Long> {
        return coreApi.getVideos(
            page = page,
            pageSize = pageSize,
            isHighlighted = isHighlighted,
            categoryId = categoryId,
        ).let { (videos, total) ->
            val rewardProgresses = coreApi.getUserVideoRewardProgresses(
                userId = userId,
                videoIds = videos.map { it.id }
            )
            Pair(
                videos.map { video -> video.toConsumerVideo(rewardProgresses.firstOrNull { it.videoId == video.id }) },
                total
            )
        }
    }

    suspend fun getVideos(
        videoIds: List<String>,
        userId: String,
    ): List<Video> {
        val rewardProgresses = coreApi.getUserVideoRewardProgresses(
            userId = userId,
            videoIds = videoIds,
        )
        return coreApi.getVideos(videoIds).data?.map { video ->
            video.toConsumerVideo(rewardProgresses.firstOrNull { it.videoId == video.id })
        }.orEmpty()
    }

    suspend fun getTrendingVideos(
        days: Int,
        limit: Int? = null,
        userId: String,
    ): List<Video> {
        return coreApi.getTrendingVideos(
            days = days,
            limit = limit
        ).let { videos ->
            val rewardProgresses = coreApi.getUserVideoRewardProgresses(
                userId = userId,
                videoIds = videos.map { it.id },
            )
            videos.map { video -> video.toConsumerVideo(rewardProgresses.firstOrNull { it.videoId == video.id }) }
        }
    }

    suspend fun getVideoDetails(
        videoId: String,
        userId: String,
    ): Video {
        val rewardProgresses = coreApi.getUserVideoRewardProgresses(
            userId = userId,
            videoIds = listOf(videoId),
        )
        return coreApi.getVideoDetails(videoId).toConsumerVideo(rewardProgresses.firstOrNull())
    }

    suspend fun getVideoComments(
        videoId: String,
        beforeCommentId: Long,
        limit: Int? = null,
    ): List<VideoComment> {
        return coreApi.getVideoComments(
            videoId = videoId,
            beforeCommentId = beforeCommentId,
            limit = limit,
        ).map { it.toConsumerVideoComment() }
    }

    //For testing purpose only. Will be removed soon
    suspend fun watchVideo(
        userId: String,
        videoProgress: VideoProgress,
    ) {
        videoPubSubService.saveProgress(userId, videoProgress)
    }
}