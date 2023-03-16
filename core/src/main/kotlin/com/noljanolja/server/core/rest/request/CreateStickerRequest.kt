package com.noljanolja.server.core.rest.request

import com.noljanolja.server.core.model.Sticker

data class CreateStickerRequest(
    val name: String,
    val publisher: String,
    val trayImageFile: String,
    val animatedStickerPack: Boolean,
    val stickers: List<Sticker>
)