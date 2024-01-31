package com.noljanolja.server.admin.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.admin.model.PromoteVideoRequest
import com.noljanolja.server.admin.model.StickerPack
import com.noljanolja.server.admin.model.VideoCreationReq
import com.noljanolja.server.admin.service.GoogleStorageService
import com.noljanolja.server.admin.service.StickerService
import com.noljanolja.server.admin.service.VideoService
import com.noljanolja.server.common.FileUtils
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.InvalidParamsException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.common.utils.extractZippedFile
import com.noljanolja.server.common.utils.readJsonFileToString
import com.noljanolja.server.common.utils.saveFileToLocal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpStatus
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.reactive.function.server.*
import java.io.File

@Configuration
class MediaHandler(
    private val stickerService: StickerService,
    private val googleStorageService: GoogleStorageService,
    private val videoService: VideoService,
    private val objectMapper: ObjectMapper
) {

    companion object {
        private const val PATH_ID = "id"
        private const val QUERY_PAGE = "page"
        private const val QUERY_PAGE_SIZE = "pageSize"
        const val DEFAULT_QUERY_PARAM_PAGE = 1
        const val DEFAULT_QUERY_PARAM_PAGE_SIZE = 10
    }

    suspend fun getStickerPacks(request: ServerRequest): ServerResponse {
        val res = stickerService.getStickerPacks()
        return ServerResponse
            .ok()
            .bodyValueAndAwait(
                Response(
                    data = res.data,
                    pagination = res.pagination
                )
            )
    }

    suspend fun getSticker(request: ServerRequest): ServerResponse {
        val stickerPackId = request.pathVariable(PATH_ID).takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(PATH_ID)
        val stickerName = request.pathVariable("stickerName").takeIf { it.isNotEmpty() }
            ?: throw DefaultBadRequestException(Error("No sticker file Name Provided"))
        val res = withContext(Dispatchers.IO) {
            val resource = googleStorageService.getResource(
                "stickers/$stickerPackId/$stickerName"
            )
            ByteArrayResource(resource.data.readAllBytes())
        }
        return ServerResponse.accepted()
            .headers {
                it.contentDisposition = ContentDisposition.attachment().filename(stickerName).build()
            }
            .bodyValueAndAwait(res)
    }

    suspend fun createStickerPack(request: ServerRequest): ServerResponse {
        val reqData = request.multipartData().awaitFirstOrNull() ?: throw RequestBodyRequired
        val stickerZip = (reqData["file"]?.firstOrNull() as? FilePart)
            ?: throw DefaultBadRequestException(Exception("Invalid file input"))
        var localFile: File? = null
        var stickerPackDir: File? = null
        try {
            localFile = stickerZip.saveFileToLocal()
            stickerPackDir = localFile.extractZippedFile("stickerFiles")

            val metadataFile = File(stickerPackDir.absoluteFile.path + File.separator + "contents.json")
            if (!metadataFile.exists()) {
                throw DefaultBadRequestException(Error("No metadata file found"))
            }
            val metadataString = metadataFile.readJsonFileToString()
            val reqStickerPack = try {
                objectMapper.readValue(metadataString, StickerPack::class.java)
            } catch (e: Exception) {
                throw DefaultBadRequestException(Error("Invalid `contents.json` file format"))
            }
            val createdStickerPack = stickerService.createStickerPack(reqStickerPack)

            FileUtils.processFileAsByteArray(
                createdStickerPack.stickers.map {
                    File(stickerPackDir.absoluteFile.path + File.separator + it.imageFile)
                }
                    .filter { it.exists() } + File(stickerPackDir.absoluteFile.path + File.separator + createdStickerPack.trayImageFile)
            ) { file, byteBuffer ->
                googleStorageService.uploadFile(
                    path = "${GoogleStorageService.STICKER_BUCKET}/${createdStickerPack.id}/${file.name}",
                    contentType = "image/${file.extension}",
                    content = byteBuffer,
                )
            }
            return ServerResponse
                .ok()
                .bodyValueAndAwait(Response(data = createdStickerPack))
        } catch (e: Exception) {
            throw e
        } finally {
            localFile?.delete()
            stickerPackDir?.deleteRecursively()
        }
    }

    suspend fun deleteStickerPack(request: ServerRequest): ServerResponse {
        val stickerPackId = request.pathVariable(PATH_ID).takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(PATH_ID)
        stickerService.deleteStickerPack(stickerPackId)
        return ServerResponse.ok().bodyValueAndAwait(Response<Nothing>())
    }

    suspend fun createVideo(request: ServerRequest): ServerResponse {
        val reqBody = request.awaitBodyOrNull<VideoCreationReq>() ?: throw RequestBodyRequired
        val res = videoService.createVideo(reqBody.youtubeUrl, reqBody.isHighlighted, reqBody.isDeactivated, reqBody.availableFrom, reqBody.availableTo)
        return ServerResponse.ok().bodyValueAndAwait(Response(data = res))
    }

    suspend fun getVideo(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull(QUERY_PAGE)?.toIntOrNull() ?: 1
        val pageSize = request.queryParamOrNull(QUERY_PAGE_SIZE)?.toIntOrNull() ?: 10
        val query = request.queryParamOrNull("query")
        val res = videoService.getVideo(query, page, pageSize)
        return ServerResponse.ok().bodyValueAndAwait(
            Response(
                data = res.data,
                pagination = res.pagination
            )
        )
    }

    suspend fun getVideoAnalytics(request: ServerRequest): ServerResponse {
        val page = request.queryParamOrNull("page")
            ?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_QUERY_PARAM_PAGE
        val pageSize = request.queryParamOrNull("pageSize")
            ?.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_QUERY_PARAM_PAGE_SIZE

        val res = videoService.getVideoAnalytics(
            page = page,
            pageSize = pageSize
        )

        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    code = HttpStatus.OK.value(),
                    data = res.data,
                    pagination = res.pagination
                )
            )
    }

    suspend fun getVideoDetail(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable(PATH_ID).takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(PATH_ID)
        val res = videoService.getVideoDetail(videoId)
        return ServerResponse.ok().bodyValueAndAwait(
            Response(
                data = res,
            )
        )
    }

    suspend fun deleteVideo(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable(PATH_ID).takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(PATH_ID)
        videoService.deleteVideo(videoId)
        return ServerResponse.ok().bodyValueAndAwait(Response<Nothing>())
    }

    suspend fun getPromotedVideos(request: ServerRequest): ServerResponse {
        val res = videoService.getPromotedVideo()
        return ServerResponse.ok().bodyValueAndAwait(
            Response(
                data = res.data,
                pagination = res.pagination
            )
        )
    }

    suspend fun updatePromotedVideo(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable(PATH_ID).takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(PATH_ID)
        val reqBody = request.awaitBodyOrNull<PromoteVideoRequest>() ?: throw RequestBodyRequired
        videoService.updatePromotedVideo(videoId, reqBody)
        return ServerResponse.ok().bodyValueAndAwait(Response<Nothing>())
    }

    suspend fun generateComments(request: ServerRequest): ServerResponse {
        val videoId = request.pathVariable(PATH_ID).takeIf { it.isNotBlank() }
            ?: throw InvalidParamsException(PATH_ID)
        val comments = videoService.generateComments(
            videoId = videoId,
        )
        return ServerResponse.ok()
            .bodyValueAndAwait(
                body = Response(
                    data = comments
                )
            )
    }
}