package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.rest.request.CreateVideoRequest
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
            body = video,
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
        val (videos, total) = videoService.getVideos(
            isHighlighted = isHighlighted,
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
}