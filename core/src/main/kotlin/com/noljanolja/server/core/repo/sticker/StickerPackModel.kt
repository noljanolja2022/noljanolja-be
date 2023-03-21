package com.noljanolja.server.core.repo.sticker

import com.noljanolja.server.core.model.Sticker
import com.noljanolja.server.core.model.StickerPack
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("sticker_packs")
data class StickerPackModel(
    @Id
    @Column("id")
    val id: Long = 0,

    @Column("name")
    val name: String,

    @Column("publisher")
    val publisher: String,

    @Column("tray_image_file")
    val trayImageFile: String,

    @Column("is_animated")
    val isAnimated: Boolean,
)  {
    fun toStickerPack(stickers: List<Sticker>) :StickerPack {
        return StickerPack(
            id = id,
            name = name,
            publisher = publisher,
            trayImageFile = trayImageFile,
            isAnimated = isAnimated,
            stickers = stickers
        )
    }
}