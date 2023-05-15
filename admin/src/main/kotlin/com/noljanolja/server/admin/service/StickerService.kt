package com.noljanolja.server.admin.service

import com.noljanolja.server.admin.adapter.core.CoreApi
import com.noljanolja.server.admin.model.StickerPack
import com.noljanolja.server.common.rest.Response
import org.springframework.context.annotation.Configuration

@Configuration
class StickerService(
    private val coreApi: CoreApi,
) {

    suspend fun getStickerPacks() : Response<List<StickerPack>> {
        return coreApi.getStickerPacks()
    }

    suspend fun createStickerPack(stickerPack: StickerPack) : StickerPack {
        val createdStickerId = coreApi.createStickerPack(stickerPack)
        return stickerPack.apply {
            id = createdStickerId
        }
    }

    suspend fun deleteStickerPack(stickerPackId: String) {
        coreApi.deleteStickerPack(stickerPackId)
    }
}