package com.noljanolja.server.consumer.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.common.utils.addFileToZipStream
import com.noljanolja.server.consumer.model.ResourceInfo
import com.noljanolja.server.consumer.service.GoogleStorageService
import com.noljanolja.server.consumer.service.MediaService
import com.noljanolja.server.consumer.utils.getStickerPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ContentDisposition
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
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
                    resources.add(googleStorageService.getResource(
                        getStickerPath(stickerPackId, it.imageFile), it.imageFile
                    ))
                }
                resources.add(googleStorageService.getResource(
                    getStickerPath(stickerPackId, stickerPack.trayImageFile), stickerPack.trayImageFile
                ))
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
}