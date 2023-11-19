package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.DefaultInternalErrorException
import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.rest.request.*
import com.noljanolja.server.core.service.ChannelService
import com.noljanolja.server.core.service.VideoService
import com.noljanolja.server.youtube.service.YoutubeCategoryService
import com.noljanolja.server.youtube.service.YoutubeChannelService
import com.noljanolja.server.youtube.service.YoutubeVideoService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class VideoHandler(
    private val videoService: VideoService,
    private val channelService: ChannelService,
    private val youtubeVideoService: YoutubeVideoService,
    private val youtubeChannelService: YoutubeChannelService,
    private val youtubeCategoryService: YoutubeCategoryService,
) {
    companion object {
        const val DEFAULT_QUERY_PARAM_PAGE = 1
        const val DEFAULT_QUERY_PARAM_PAGE_SIZE = 10
    }

    suspend fun upsertVideo(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<CreateVideoRequest>() ?: throw RequestBodyRequired
        val youtubeVideo = youtubeVideoService.fetchVideoDetail(listOf(payload.id)).items.firstOrNull()
            ?: throw DefaultInternalErrorException(Exception("Unable to retrieve youtube video"))
        val youtubeCategory = youtubeCategoryService.fetchCategory(youtubeVideo.snippet.categoryId).items.firstOrNull()
            ?: throw DefaultInternalErrorException(Exception("Unable to retrieve category of the video"))
        val youtubeChannel =
            youtubeChannelService.fetchChannelDetail(youtubeVideo.snippet.channelId).items.firstOrNull()
                ?: throw DefaultInternalErrorException(Exception("Unable to retrieve channel of the video"))
        val res = videoService.upsertVideo(
            payload.youtubeUrl,
            payload.isHighlighted,
            youtubeVideo,
            youtubeChannel,
            youtubeCategory
        )
        return ServerResponse.ok().bodyValueAndAwait(
            body = Response(
                data = res,
            ),
        )
    }

    suspend fun deleteVideo(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        videoService.deleteVideo(videoId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun getVideos(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_QUERY_PARAM_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 }
            ?: DEFAULT_QUERY_PARAM_PAGE_SIZE
        val isHighlighted = request.queryParamOrNull("isHighlighted")?.toBoolean()
        val categoryId = request.queryParamOrNull("categoryId")
        val query = request.queryParamOrNull("query")
        val userId = request.queryParamOrNull("userId")
        val isExcludeIgnoredVideos = request.queryParamOrNull("isExcludeIgnoredVideos")?.toBoolean()
        val (videos, total) = videoService.getVideos(
            isHighlighted = isHighlighted,
            query = query,
            page = page,
            pageSize = pageSize,
            categoryId = categoryId,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = videos,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total,
                    )
                )
            )
    }

    suspend fun getWatchingVideos(request: ServerRequest): ServerResponse {
        val videoIds = request.queryParamOrNull("videoIds") ?: ""
        val userId = request.queryParamOrNull("userId")
        val isExcludeIgnoredVideos = request.queryParamOrNull("isExcludeIgnoredVideos")?.toBoolean()
        val data = videoService.getVideosByIds(
            ids = videoIds.split(","),
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                Response(
                    data = data
                )
            )
    }

    suspend fun getVideoDetails(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        val video = videoService.getVideoDetails(videoId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = video
                )
            )
    }

    suspend fun viewVideo(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        videoService.viewVideo(videoId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun getTrendingVideos(request: ServerRequest): ServerResponse {
        val days = request.queryParamOrNull("days")?.toIntOrNull()?.takeIf { it > 0 }
            ?: throw InvalidParamsException("days")
        val limit = request.queryParamOrNull("limit")?.toIntOrNull()?.takeIf { it > 0 } ?: 10
        val userId = request.queryParamOrNull("userId")
        val isExcludeIgnoredVideos = request.queryParamOrNull("isExcludeIgnoredVideos")?.toBoolean()
        val videos = videoService.getTrendingVideos(
            days = days,
            limit = limit,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = videos
                )
            )
    }

    suspend fun ignoreVideo(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        val req = request.awaitBodyOrNull<IgnoreVideoRequest>()
        val userId = req?.userId?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("userId")

        videoService.ignoreVideo(
            videoId = videoId,
            userId = userId
        )

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun likeVideo(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        val req = request.awaitBodyOrNull<LikeVideoRequest>()
        val userId = req?.userId?.takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("userId")
        val action = req.action
        videoService.likeVideo(
            videoId = videoId,
            userId = userId,
            action = action,
            youtubeToken = req.youtubeToken
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun postComment(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        val payload = request.awaitBodyOrNull<PostCommentRequest>() ?: throw RequestBodyRequired
        val comment = videoService.postComment(
            token = payload.youtubeToken,
            comment = payload.comment,
            commenterId = payload.commenterId,
            videoId = videoId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = comment,
                )
            )
    }

    suspend fun getVideoComments(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        val beforeCommentId = request.queryParamOrNull("beforeCommentId")?.toLongOrNull()?.takeIf { it > 0 }
            ?: throw InvalidParamsException("beforeCommentId")
        val limit = request.queryParamOrNull("limit")?.toIntOrNull()?.takeIf { it > 0 } ?: 10
        val comments = videoService.getVideoComments(
            videoId = videoId,
            beforeCommentId = beforeCommentId,
            limit = limit,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = comments,
                )
            )
    }

    suspend fun getPromotedVideos(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")?.toIntOrNull() ?: DEFAULT_QUERY_PARAM_PAGE
        val pageSize = request.queryParamOrNull("pageSize")?.toIntOrNull() ?: DEFAULT_QUERY_PARAM_PAGE_SIZE
        val userId = request.queryParamOrNull("userId")
        val isExcludeIgnoredVideos = request.queryParamOrNull("isExcludeIgnoredVideos")?.toBoolean()
        val (videos, total) = videoService.getPromotedVideos(
            page = page,
            pageSize = pageSize,
            userId = userId,
            isExcludeIgnoredVideos = isExcludeIgnoredVideos
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = videos,
                    pagination = Pagination(
                        page = page,
                        pageSize = pageSize,
                        total = total,
                    )
                )
            )
    }

    suspend fun promoteVideo(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId").ifBlank { throw InvalidParamsException("videoId") }
        val payload = request.awaitBodyOrNull<PromoteVideoRequest>() ?: throw RequestBodyRequired
        videoService.promoteVideo(
            videoId = videoId,
            payload
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun reactToPromotedVideo(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId").ifBlank { throw InvalidParamsException("videoId") }
        val payload = request.awaitBodyOrNull<ReactToPromotedVideoReq>() ?: throw RequestBodyRequired
        videoService.reactToPromotedVideo(
            videoId = videoId,
            youtubeToken = payload.youtubeToken,
            userId = payload.userId
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun getChannelDetail(request: ServerRequest): ServerResponse {
        val channelId = request.pathVariable("channelId").ifBlank { throw InvalidParamsException("channelId") }
        val res = channelService.getChannelDetail(channelId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = res
                )
            )
    }

    suspend fun subscribeToChannel(request: ServerRequest): ServerResponse {
        val channelId = request.pathVariable("channelId").ifBlank { throw InvalidParamsException("channelId") }
        val payload = request.awaitBodyOrNull<SubscribeChannelRequest>() ?: throw RequestBodyRequired
        if (payload.isSubscribing) {
            channelService.addSubscription(
                channelId, payload.userId, payload.youtubeToken
            )
        } else {
            channelService.removeSubscription(
                channelId, payload.userId, payload.youtubeToken
            )
        }

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun upsertVideoGeneratedComments(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        val comments = request.awaitBodyOrNull<List<String>>() ?: throw RequestBodyRequired
        videoService.upsertGeneratedComments(
            videoId = videoId,
            comments = comments,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}