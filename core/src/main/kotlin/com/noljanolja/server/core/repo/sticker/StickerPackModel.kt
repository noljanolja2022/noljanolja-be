package com.noljanolja.server.core.repo.sticker

import com.noljanolja.server.core.model.Sticker
import com.noljanolja.server.core.model.StickerPack
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("sticker_packs")
data class StickerPackModel(
    @Id
    @Column("id")
    val _id: Long = 0,

    @Column("name")
    val name: String,

    @Column("publisher")
    val publisher: String,

    @Column("tray_image_file")
    val trayImageFile: String,

    @Column("animated_sticker_pack")
    val animatedStickerPack: Boolean,
) : Persistable<Long> {
    @Transient
    var isNewRecord = false
    override fun getId(): Long = _id

    override fun isNew(): Boolean = isNewRecord

    fun toStickerPack(stickers: List<Sticker>) :StickerPack {
        return StickerPack(
            id = id,
            name = name,
            publisher = publisher,
            trayImageFile = trayImageFile,
            animatedStickerPack = animatedStickerPack,
            stickers = stickers
        )
    }
}