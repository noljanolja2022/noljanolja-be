package com.noljanolja.server.admin.rest

import com.noljanolja.server.admin.service.GoogleStorageService
import com.noljanolja.server.admin.service.StickerService
import com.noljanolja.server.common.FileUtils
import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.exception.RequestBodyRequired
import com.noljanolja.server.common.rest.Response
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
    private val googleStorageService: GoogleStorageService
) {
    suspend fun createStickerPack(request: ServerRequest) : ServerResponse {
        val reqData = request.multipartData().awaitFirstOrNull() ?: throw RequestBodyRequired
        val stickerZip = (reqData["file"]?.firstOrNull() as? FilePart) ?: throw DefaultBadRequestException(Exception("Invalid file input"))

        val localFile = FileUtils.saveFileToLocal(stickerZip)
        val stickerPackDir = FileUtils.extractZippedFile(localFile, "stickerFiles")
        localFile.delete()
        val stickerPack = stickerService.createStickerPack(stickerPackDir)

        FileUtils.processFileAsByteArray(
            stickerPack.stickers.map {
                File(stickerPackDir.absoluteFile.path + File.separator + it.imageFile)
            }.filter { it.exists() }
        ) { file, byteBuffer ->
            googleStorageService.uploadFile(
                path = "stickers/${stickerPack.id}/${file.name}",
                contentType = "image/${file.extension}",
                content = byteBuffer,
            )
        }
        stickerPackDir.deleteRecursively()
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response(data = stickerPack))
    }
}