package com.noljanolja.server.consumer.service

import com.noljanolja.server.consumer.adapter.core.CoreApi
import com.noljanolja.server.consumer.model.StickerPack
import org.springframework.stereotype.Component

@Component
class MediaService(
    private val coreApi: CoreApi,
) {
    suspend fun getAllStickerPacks(userId: String) : List<StickerPack> {
        return coreApi.getAllStickerPacksFromUser(userId)!!
    }

    suspend fun getStickerPack(stickerPackId: Long): StickerPack {
        return coreApi.getStickerPack(stickerPackId)!!
    }
}