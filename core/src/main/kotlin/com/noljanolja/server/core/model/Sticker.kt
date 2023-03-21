package com.noljanolja.server.core.model

data class Sticker(
    val imageFile: String,
    val emojis: List<String> = emptyList()
)

data class StickerPack(
    val id: Long,
    val name: String,
    val publisher: String,
    val trayImageFile: String,
    val isAnimated: Boolean,
    val stickers: List<Sticker>
)