package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.rest.request.CreateStickerRequest
import com.noljanolja.server.core.service.StickerService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class MediaHandler(
    private val stickerService: StickerService
) {
    suspend fun getStickerPacks(serverRequest: ServerRequest): ServerResponse {
        val res = stickerService.getStickerPacks()
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response(data = res))
    }

    suspend fun getStickerPack(serverRequest: ServerRequest): ServerResponse {
        val packId = serverRequest.pathVariable("packId").toLong()
        val res = stickerService.getStickerPack(packId)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response(data = res))
    }

    suspend fun createStickerPack(serverRequest: ServerRequest): ServerResponse {
        val body = serverRequest.awaitBodyOrNull<CreateStickerRequest>() ?: throw DefaultBadRequestException(null)
        if (body.stickers.isEmpty()) {
            throw Error.StickersNotFound
        }
        val res = stickerService.createStickerPack(body)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response(data = res.id))
    }

    suspend fun deleteStickerPack(serverRequest: ServerRequest): ServerResponse {
        val packId = serverRequest.pathVariable("packId").toLong()
        stickerService.deleteStickerPack(packId)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response<Nothing>())
    }
}