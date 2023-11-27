package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.*
import com.noljanolja.server.consumer.adapter.core.request.CoreIgnoreVideoRequest
import com.noljanolja.server.consumer.adapter.core.request.CoreLikeVideoRequest
import com.noljanolja.server.consumer.adapter.core.request.PostCommentRequest
import com.noljanolja.server.consumer.model.*
import com.noljanolja.server.consumer.rsocket.SocketRequester
import com.noljanolja.server.consumer.rsocket.UserVideoComment
import com.noljanolja.server.consumer.rsocket.UserVideoLike
import org.springframework.stereotype.Component

@Component
class MediaService(
    private val coreApi: CoreApi,
    private val videoPubSubService: VideoPubSubService,
    private val socketRequester: SocketRequester,
) {
    suspend fun getAllStickerPacks(userId: String): List<StickerPack> {
        return coreApi.getAllStickerPacksFromUser(userId)!!
    }

    suspend fun getStickerPack(stickerPackId: Long): StickerPack {
        return coreApi.getStickerPack(stickerPackId)!!
    }

    suspend fun getChannelDetail(channelId: String): Channel? {
        return coreApi.getChannelDetail(
            channelId = channelId,
        ).data?.toChannel()
    }

    suspend fun subscribeToChannel(channelId: String, userId: String, youtubeToken: String, isSubscribing: Boolean) {
        coreApi.subscribeToChannel(
            youtubeToken,
            userId = userId,
            channelId = channelId,
            isSubscribing = isSubscribing
        )
    }

    suspend fun ignoreVideo(
        videoId: String,
        userId: String
    ) {
        coreApi.ignoreVideo(
            videoId = videoId,
            payload = CoreIgnoreVideoRequest(userId)
        )
    }

    suspend fun likeVideo(
        videoId: String,
        userId: String,
        action: RateVideoAction,
        youtubeToken: String? = null
    ) {
        coreApi.likeVideo(
            videoId = videoId,
            payload = CoreLikeVideoRequest(action, userId, youtubeToken)
        )
        socketRequester.emitUserLikeVideo(
            UserVideoLike(
                action = action,
                userId = userId,
                videoId = videoId,
            )
        )
    }

    suspend fun postComment(
        comment: String,
        userId: String,
        videoId: String,
        youtubeToken: String,
    ): VideoComment {
        val videoComment = coreApi.postComment(
            videoId = videoId,
            payload = PostCommentRequest(
                commenterId = userId,
                comment = comment,
                youtubeToken = youtubeToken
            )
        ).toConsumerVideoComment()
        socketRequester.emitUserCommentVideo(
            UserVideoComment(
                userId = userId,
                videoId = videoId,
                comment = comment,
            )
        )
        return videoComment
    }

    suspend fun getVideos(
        page: Int,
        pageSize: Int,
        query: String? = null,
        isHighlighted: Boolean? = null,
        categoryId: String? = null,
        userId: String,
        isExcludeIgnoredVideos: Boolean
    ): Pair<List<Video>, Long> {
        return coreApi.getVideos(
            query = query,
            page = page,
            pageSize = pageSize,
            isHighlighted = isHighlighted,
            categoryId = categoryId,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos
        ).let { (videos, total) ->
            val rewardProgresses = if (videos.isNotEmpty()) coreApi.getUserVideoRewardProgresses(
                userId = userId,
                videoIds = videos.map { it.id }
            ) else emptyList()
            Pair(
                videos.map { video -> video.toConsumerVideo(rewardProgresses.firstOrNull { it.videoId == video.id }) },
                total
            )
        }
    }

    suspend fun getVideos(
        videoIds: List<String>,
        userId: String,
        isExcludeIgnoredVideos: Boolean
    ): List<Video> {
        val rewardProgresses = coreApi.getUserVideoRewardProgresses(
            userId = userId,
            videoIds = videoIds,
        )
        return coreApi.getVideos(
            videoIds = videoIds,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos
        ).data?.map { video ->
            video.toConsumerVideo(rewardProgresses.firstOrNull { it.videoId == video.id })
        }.orEmpty()
    }

    suspend fun getTrendingVideos(
        days: Int,
        limit: Int? = null,
        userId: String,
        isExcludeIgnoredVideos: Boolean
    ): List<Video> {
        return coreApi.getTrendingVideos(
            days = days,
            limit = limit,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos
        ).let { videos ->
            val rewardProgresses = if (videos.isNotEmpty()) coreApi.getUserVideoRewardProgresses(
                userId = userId,
                videoIds = videos.map { it.id },
            ) else emptyList()
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
        return coreApi.getVideoDetail(videoId).toConsumerVideo(rewardProgresses.firstOrNull())
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
        videoPubSubService.updateWatchProgress(userId, videoProgress)
    }

    suspend fun getPromotedVideos(
        page: Int,
        pageSize: Int,
        userId: String,
        isExcludeIgnoredVideos: Boolean
    ): List<CorePromotedVideoConfig> = coreApi.getPromotedVideos(
        page = page,
        pageSize = pageSize,
        userId = userId,
        isExcludeIgnoredVideos = isExcludeIgnoredVideos
    )

    suspend fun reactToPromotedVideo(videoId: String, youtubeToken: String, userId: String) {
        coreApi.reactToPromotedVideo(videoId, youtubeToken, userId)
    }
}