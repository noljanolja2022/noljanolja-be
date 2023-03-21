package com.noljanolja.server.admin.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.model.StickerPack
import org.springframework.context.annotation.Configuration

@Configuration
class StickerService(
    private val coreApi: CoreApi,
) {

    suspend fun createStickerPack(stickerPack: StickerPack) : StickerPack {
        val createdStickerId = coreApi.createStickerPack(stickerPack)
        return stickerPack.apply {
            id = createdStickerId
        }
    }
}