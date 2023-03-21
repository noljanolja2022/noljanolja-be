package com.noljanolja.server.consumer.model

data class StickerPack(
    var id: Long?,
    val name: String,
    val publisher: String,
    val trayImageFile: String,
    val isAnimated: Boolean,
    val stickers: List<Sticker> = emptyList()
)

data class Sticker(
    val imageFile: String,
    val emojis: List<String> = emptyList()
)