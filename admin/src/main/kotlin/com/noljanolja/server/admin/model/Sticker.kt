package com.noljanolja.server.admin.model

import com.google.gson.annotations.SerializedName

data class StickerPack(
    var id: Long?,
    val name: String,
    val publisher: String,
    @SerializedName("tray_image_file")
    val trayImageFile: String,
    @SerializedName("animated_sticker_pack")
    val animatedStickerPack: Boolean,
    val stickers: List<Sticker> = emptyList()
)

data class Sticker(
    @SerializedName("image_file")
    val imageFile: String,
    val emojis: List<String> = emptyList()
)