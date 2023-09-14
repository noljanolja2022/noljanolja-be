package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.rest.request.CreateVideoRequest
import com.noljanolja.server.core.rest.request.LikeVideoRequest
import com.noljanolja.server.core.rest.request.PostCommentRequest
import com.noljanolja.server.core.rest.request.PromoteVideoRequest
import com.noljanolja.server.core.service.VideoService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class VideoHandler(
    private val videoService: VideoService,
) {
    companion object {
        const val DEFAULT_QUERY_PARAM_PAGE = 1
        const val DEFAULT_QUERY_PARAM_PAGE_SIZE = 10
    }

    suspend fun upsertVideo(request: ServerRequest): ServerResponse {
        val payload = request.awaitBodyOrNull<CreateVideoRequest>() ?: throw RequestBodyRequired
        val video = videoService.upsertVideo(
            videoInfo = payload,
        )
        return ServerResponse.ok().bodyValueAndAwait(
            body = Response(
                data = video,
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
        val (videos, total) = videoService.getVideos(
            isHighlighted = isHighlighted,
            query = query,
            page = page,
            pageSize = pageSize,
            categoryId = categoryId,
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
        val data = videoService.getVideosByIds(videoIds.split(","))
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
        val videos = videoService.getTrendingVideos(
            days = days,
            limit = limit,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = videos
                )
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
            action = action
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
        val (videos, total) = videoService.getPromotedVideos(
            page = page,
            pageSize = pageSize,
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
            startDate = payload.startDate,
            endDate = payload.endDate,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }
}