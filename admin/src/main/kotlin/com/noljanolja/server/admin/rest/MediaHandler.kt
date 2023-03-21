package com.noljanolja.server.admin.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.admin.model.StickerPack
import com.noljanolja.server.admin.service.GoogleStorageService
import com.noljanolja.server.admin.service.StickerService
import com.noljanolja.server.common.FileUtils
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.common.utils.extractZippedFile
import com.noljanolja.server.common.utils.readJsonFileToString
import com.noljanolja.server.common.utils.saveFileToLocal
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.io.File

@Configuration
class MediaHandler(
    private val stickerService: StickerService,
    private val googleStorageService: GoogleStorageService,
    private val objectMapper: ObjectMapper
) {

    suspend fun createStickerPack(request: ServerRequest) : ServerResponse {
        val reqData = request.multipartData().awaitFirstOrNull() ?: throw RequestBodyRequired
        val stickerZip = (reqData["file"]?.firstOrNull() as? FilePart) ?: throw DefaultBadRequestException(Exception("Invalid file input"))
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
                }.filter { it.exists() } + File(stickerPackDir.absoluteFile.path + File.separator + createdStickerPack.trayImageFile)
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
}