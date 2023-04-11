package com.noljanolja.server.consumer.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.model.Pagination
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.common.utils.addFileToZipStream
import com.noljanolja.server.consumer.filter.AuthUserHolder
import com.noljanolja.server.consumer.model.ResourceInfo
import com.noljanolja.server.consumer.rest.request.PostCommentRequest
import com.noljanolja.server.consumer.service.GoogleStorageService
import com.noljanolja.server.consumer.service.MediaService
import com.noljanolja.server.consumer.utils.getStickerPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ContentDisposition
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.io.ByteArrayOutputStream
import java.util.zip.ZipOutputStream

@Component
class MediaHandler(
    private val googleStorageService: GoogleStorageService,
    private val mediaService: MediaService,
    private val objectMapper: ObjectMapper,
) {
    suspend fun getAllStickerPacks(serverRequest: ServerRequest): ServerResponse {
        val stickerPacks = mediaService.getAllStickerPacks("to_be_replaced")
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response(data = stickerPacks))
    }

    suspend fun getStickerPack(serverRequest: ServerRequest): ServerResponse {
        val stickerPackId = serverRequest.pathVariable("stickerPackId").toLongOrNull()
            ?: throw DefaultBadRequestException(Error("No sticker pack Id Provided"))
        val stickerPack = mediaService.getStickerPack(stickerPackId)

        val ops = ByteArrayOutputStream(1024 * 1024 * 10)
        withContext(Dispatchers.IO) {
            val zipOutputStream = ZipOutputStream(ops)
            try {
                val resources = mutableListOf<ResourceInfo>()
                stickerPack.stickers.forEach {
                    resources.add(
                        googleStorageService.getResource(
                            getStickerPath(stickerPackId, it.imageFile), it.imageFile
                        )
                    )
                }
                resources.add(
                    googleStorageService.getResource(
                        getStickerPath(stickerPackId, stickerPack.trayImageFile), stickerPack.trayImageFile
                    )
                )
                resources.forEach {
                    zipOutputStream.addFileToZipStream(it.data.readAllBytes(), it.fileName ?: "")
                }
                val metadata = objectMapper.writeValueAsBytes(stickerPack)
                zipOutputStream.addFileToZipStream(metadata, "contents.json")
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            } finally {
                zipOutputStream.closeEntry()
                zipOutputStream.close()
                ops.close()
            }
        }
        val res = ByteArrayResource(ops.toByteArray())
        return ServerResponse
            .ok()
            .headers {
                it.contentDisposition = ContentDisposition.attachment().filename("$stickerPackId.zip").build()
            }
            .bodyValueAndAwait(res)
    }

    suspend fun getSticker(serverRequest: ServerRequest): ServerResponse {
        val stickerPackId = serverRequest.pathVariable("stickerPackId").toLongOrNull()
            ?: throw DefaultBadRequestException(Error("No sticker pack Id Provided"))
        val stickerName = serverRequest.pathVariable("stickerFileName").takeIf { it.isNotEmpty() }
            ?: throw DefaultBadRequestException(Error("No sticker file Name Provided"))
        val res = withContext(Dispatchers.IO) {
            val resource = googleStorageService.getResource(
                getStickerPath(stickerPackId, stickerName)
            )
            ByteArrayResource(resource.data.readAllBytes())
        }
        return ServerResponse.accepted()
            .headers {
                it.contentDisposition = ContentDisposition.attachment().filename(stickerName).build()
            }
            .bodyValueAndAwait(res)
    }

    suspend fun likeVideo(serverRequest: ServerRequest): ServerResponse {
        val videoId = serverRequest.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        val userId = AuthUserHolder.awaitUser().id
        mediaService.likeVideo(
            videoId = videoId,
            userId = userId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response<Nothing>()
            )
    }

    suspend fun postComment(serverRequest: ServerRequest): ServerResponse {
        val videoId = serverRequest.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        val userId = AuthUserHolder.awaitUser().id
        val payload = serverRequest.awaitBodyOrNull<PostCommentRequest>() ?: throw RequestBodyRequired
        val comment = mediaService.postComment(
            comment = payload.comment,
            userId = userId,
            videoId = videoId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = comment
                )
            )
    }

    suspend fun getVideos(serverRequest: ServerRequest): ServerResponse {
        val page = serverRequest.queryParamOrNull("page")?.toIntOrNull()?.takeIf { it > 0 } ?: 1
        val pageSize = serverRequest.queryParamOrNull("pageSize")?.toIntOrNull()?.takeIf { it > 0 }
            ?: 10
        val isHighlighted = serverRequest.queryParamOrNull("isHighlighted")?.toBoolean()
        val categoryId = serverRequest.queryParamOrNull("categoryId")
        val (videos, total) = mediaService.getVideos(
            isHighlighted = isHighlighted,
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

    suspend fun getVideoDetails(serverRequest: ServerRequest): ServerResponse {
        val videoId = serverRequest.pathVariable("videoId")
        val video = mediaService.getVideoDetails(videoId)
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = video,
                )
            )
    }

    suspend fun getTrendingVideos(serverRequest: ServerRequest): ServerResponse {
        val days = when (serverRequest.queryParamOrNull("duration")) {
            "day" -> 1
            "week" -> 7
            "month" -> 30
            else -> throw InvalidParamsException("duration")
        }
        val limit = serverRequest.queryParamOrNull("limit")?.toIntOrNull()?.takeIf { it > 0 } ?: 10
        val videos = mediaService.getTrendingVideos(
            days = days,
            limit = limit,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = videos,
                )
            )
    }

    suspend fun getVideoComments(serverRequest: ServerRequest): ServerResponse {
        val videoId = serverRequest.pathVariable("videoId").takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException("videoId")
        val beforeCommentId = serverRequest.queryParamOrNull("beforeCommentId")?.toLongOrNull()?.takeIf { it > 0 }
            ?: throw InvalidParamsException("beforeCommentId")
        val limit = serverRequest.queryParamOrNull("limit")?.toIntOrNull()?.takeIf { it > 0 } ?: 10
        val comments = mediaService.getVideoComments(
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
}