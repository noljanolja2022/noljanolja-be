package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.adapter.core.request.LikeVideoRequest
import com.noljanolja.server.consumer.adapter.core.request.PostCommentRequest
import com.noljanolja.server.consumer.adapter.core.toConsumerVideo
import com.noljanolja.server.consumer.adapter.core.toConsumerVideoComment
import com.noljanolja.server.consumer.model.StickerPack
import com.noljanolja.server.consumer.model.Video
import com.noljanolja.server.consumer.model.VideoComment
import org.springframework.stereotype.Component

@Component
class MediaService(
    private val coreApi: CoreApi,
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
    ): VideoComment {
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
    ): Pair<List<Video>, Long> {
        return coreApi.getVideos(
            page = page,
            pageSize = pageSize,
            isHighlighted = isHighlighted,
            categoryId = categoryId,
        ).let {
            Pair(it.first.map { it.toConsumerVideo() }, it.second)
        }
    }

    suspend fun getVideos(
        videoIds: List<String>
    ): List<Video> {
        return coreApi.getVideos(videoIds).data?.map { it.toConsumerVideo() } ?: emptyList()
    }

    suspend fun getTrendingVideos(
        days: Int,
        limit: Int? = null,
    ): List<Video> {
        return coreApi.getTrendingVideos(
            days = days,
            limit = limit
        ).map { it.toConsumerVideo() }
    }

    suspend fun getVideoDetails(
        videoId: String
    ): Video {
        return coreApi.getVideoDetails(videoId).toConsumerVideo()
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
}