package com.noljanolja.server.core.rest

import com.noljanolja.server.common.exception.DefaultBadRequestException
import com.noljanolja.server.common.rest.Response
import com.noljanolja.server.core.rest.request.CreateStickerRequest
import com.noljanolja.server.core.service.StickerService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

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
        val packId = serverRequest.queryParamOrNull("packId")?.toLong()
            ?: throw DefaultBadRequestException(null)
        val res = stickerService.getStickerPack(packId)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response(data = res))
    }

    suspend fun createStickerPack(serverRequest: ServerRequest): ServerResponse {
        val body = serverRequest.awaitBodyOrNull<CreateStickerRequest>() ?: throw DefaultBadRequestException(null)
        if (body.stickers.isEmpty()) {
            throw DefaultBadRequestException(null)
        }
        val res = stickerService.createStickerPack(body)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response(data = res.id))
    }

    suspend fun deleteStickerPack(serverRequest: ServerRequest): ServerResponse {
        val packId = serverRequest.queryParamOrNull("packId")?.toLong() ?: throw DefaultBadRequestException(null)
        stickerService.deleteStickerPack(packId)
        return ServerResponse
            .ok()
            .bodyValueAndAwait(Response<Nothing>())
    }
}